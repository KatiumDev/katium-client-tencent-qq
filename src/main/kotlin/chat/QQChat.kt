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
package katium.client.qq.chat

import katium.client.qq.QQBot
import katium.client.qq.QQLocalChatID
import katium.client.qq.message.QQMessage
import katium.client.qq.message.QQMessageRef
import katium.client.qq.network.packet.messageSvc.SendMessageRequest
import katium.client.qq.network.pb.PbMessagePackets
import katium.core.chat.Chat
import katium.core.chat.Chattable
import katium.core.event.MessagePreSendEvent
import katium.core.event.MessageSentEvent
import katium.core.message.MessageRef
import katium.core.message.content.MessageContent
import katium.core.user.User
import katium.core.util.event.post

class QQChat(override val bot: QQBot, id: Long, context: Chattable, val routingHeader: PbMessagePackets.RoutingHeader) :
    Chat(bot, QQLocalChatID(id), context) {

    override val name: String
        get() = "Unknown"
    override val members: Set<User>
        get() = TODO("Not yet implemented")

    override suspend fun sendMessage(content: MessageContent): MessageRef? {
        val client = bot.client
        return bot.post(MessagePreSendEvent(bot, this, content.simplest))?.let {
            val message = QQMessage(bot, this@QQChat, bot.selfInfo, it.content, System.currentTimeMillis())
            client.send(
                SendMessageRequest.create(
                    client,
                    routingHeader = routingHeader,
                    elements = client.messageEncoders.encode(this, it.content)
                )
            )
            bot.post(MessageSentEvent(message))
            QQMessageRef(bot, message)
        }
    }

    override suspend fun removeMessage(message: MessageRef) {
        TODO("Not yet implemented")
    }

}