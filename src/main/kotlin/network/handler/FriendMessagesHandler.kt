/*
 * Copyright 2022 Katium Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package katium.client.qq.network.handler

import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.client.qq.network.event.QQReceivedRawMessageEvent
import katium.client.qq.network.packet.chat.DeleteMessagesRequest
import katium.client.qq.network.packet.chat.PullMessagesRequest
import katium.client.qq.network.packet.chat.PullMessagesResponse
import katium.client.qq.network.packet.chat.PushNotifyPacket
import katium.client.qq.network.sync.SyncFlag
import katium.core.util.event.Subscribe
import katium.core.util.event.post
import katium.core.util.netty.toArray
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object FriendMessagesHandler : QQClientHandler {

    override val id: String get() = "friend_messages_handler"

    @Subscribe
    suspend fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is PushNotifyPacket) {
            val data = packet.data
            client.send(PullMessagesRequest.create(client, syncCookies = data.notifyCookie.toArray(release = false)))
        } else if (packet is PullMessagesResponse) {
            val response = packet.response
            if (response.result != 0) { // Retry if error
                client.launch(CoroutineName("Retry Pull Messages")) {
                    delay(5000L)
                    client.send(PullMessagesRequest.create(client))
                }
            }
            val synchronzier = client.synchronzier
            when (response.syncType) {
                0 -> {
                    synchronzier.syncCookie = response.syncCookie
                    synchronzier.publicAccountCookie = response.publicAccountCookie
                }
                1 -> synchronzier.syncCookie = response.syncCookie
                2 -> synchronzier.publicAccountCookie = response.publicAccountCookie
                else -> throw UnsupportedOperationException("Unknown sync type: ${response.syncType}")
            }
            val isInitialSync = synchronzier.friendInitialSync.getAndSet(false)
            if (response.messages.isEmpty()) return
            val messages = response.messages.asSequence().filterNot { it.messages.isEmpty() }.flatMap { pair ->
                pair.messages.asSequence().filter { it.header.time > pair.lastReadTime }
            }.toList().also {
                // Delete messages
                if (it.isNotEmpty()) {
                    client.send(DeleteMessagesRequest.create(client, items = it.map { message ->
                        DeleteMessagesRequest.Item(
                            fromUin = message.header.fromUin,
                            toUin = message.header.toUin,
                            type = 187,
                            sequence = message.header.sequence,
                            uid = message.header.uid,
                            sig = ByteArray(0)
                        )
                    }))
                }
            }.filter {
                synchronzier.writePullMessagesCache(
                    it.header.uid, it.header.sequence, it.header.time
                )
            }
            synchronzier.recordUnreadFriendMessages(response.messages.map { it.peerUin })
            if (isInitialSync) return
            for (message in messages.map { QQReceivedRawMessageEvent(client, it) }) {
                client.bot.post(message)
            }
            if (response.syncFlag != SyncFlag.STOP) { // Continue
                client.send(PullMessagesRequest.create(client, syncFlag = response.syncFlag))
            }
        }
    }

}