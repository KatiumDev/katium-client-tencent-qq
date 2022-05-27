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

import com.google.protobuf.ByteString
import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.client.qq.network.packet.messageSvc.DeleteMessagesRequest
import katium.client.qq.network.packet.messageSvc.PullMessagesRequest
import katium.client.qq.network.packet.messageSvc.PullMessagesResponse
import katium.client.qq.network.packet.messageSvc.PushNotifyPacket
import katium.client.qq.network.pb.PbDeleteMessages
import katium.client.qq.network.pb.PbMessages
import katium.core.event.ReceivedMessageEvent
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import katium.core.util.event.post
import katium.core.util.netty.toArray
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object MessagesHandler : EventListener {

    @Subscribe
    fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is PushNotifyPacket) {
            val data = packet.data
            client.send(
                PullMessagesRequest.create(
                    client,
                    syncFlag = PbMessages.SyncFlag.START,
                    syncCookies = ByteString.copyFrom(data.notifyCookie.toArray(false))
                )
            )
        }
        if (packet is PullMessagesResponse) {
            val response = packet.response
            if (response.result != 0) { // Retry if error
                client.launch(CoroutineName("Retry Pull Messages")) {
                    delay(5000L)
                    client.send(PullMessagesRequest.create(client))
                }
            }
            when (response.syncType) {
                0 -> {
                    client.synchronzier.syncCookie = response.syncCookie
                    client.synchronzier.publicAccountCookie = response.publicAccountCookie
                }
                1 -> client.synchronzier.syncCookie = response.syncCookie
                2 -> client.synchronzier.publicAccountCookie = response.publicAccountCookie
                else -> throw UnsupportedOperationException("Unknown sync type: ${response.syncType}")
            }
            if (response.messagesList.isEmpty()) return
            response.messagesList.asSequence()
                .filterNot { it.messagesList.isEmpty() }
                .flatMap { pair ->
                    pair.messagesList.asSequence()
                        .filter { it.header.time > pair.lastReadTime }
                }
                .toList()
                .also {
                    // Delete messages
                    client.send(DeleteMessagesRequest.create(client, items = it.map {
                        PbDeleteMessages.MessageItem.newBuilder().apply {
                            fromUin = it.header.fromUin
                            toUin = it.header.toUin
                            type = it.header.type
                            sequence = it.header.sequence
                            uid = it.header.uid
                        }.build()
                    }))
                }
                .filter {
                    client.synchronzier.writePullMessagesCache(
                        it.header.uid,
                        it.header.sequence,
                        it.header.time
                    )
                }
                .forEach {
                    client.launch(CoroutineName("Handle Received Message")) {
                        client.bot.post(
                            ReceivedMessageEvent(
                                (client.messageDecoders[it.header.type]
                                    ?: throw UnsupportedOperationException("Unknown message type: ${it.header.type}"))
                                    .decode(client, it)
                            )
                        )
                    }
                }
            if (response.syncFlag != PbMessages.SyncFlag.STOP) { // Continue
                client.send(PullMessagesRequest.create(client, syncFlag = response.syncFlag))
            }
        }
    }

}