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
import katium.client.qq.network.packet.onlinePush.PushGroupMessagesPacket
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import katium.core.util.event.post

object GroupMessagesHandler : EventListener {

    @Subscribe
    suspend fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is PushGroupMessagesPacket) {
            val response = packet.response
            val message = response.message
            client.synchronzier.recordUnreadGroupMessage(
                message.header.groupInfo.groupCode,
                message.header.sequence.toLong()
            )
            client.bot.post(QQReceivedRawMessageEvent(client, message))
        }
    }

}