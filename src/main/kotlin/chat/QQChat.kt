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
import katium.client.qq.group.QQGroup
import katium.client.qq.message.QQMessage
import katium.client.qq.network.packet.chat.SendMessageRequest
import katium.client.qq.network.packet.chat.SendMessageResponse
import katium.client.qq.network.pb.PbMessagePackets
import katium.client.qq.user.QQContact
import katium.core.chat.Chat
import katium.core.chat.ChatInfo
import katium.core.event.MessagePreSendEvent
import katium.core.message.MessageRef
import katium.core.message.content.MessageContent
import katium.core.util.event.post
import kotlinx.coroutines.delay
import kotlin.random.Random

class QQChat(override val bot: QQBot, id: Long, context: ChatInfo, val routingHeader: PbMessagePackets.RoutingHeader) :
    Chat(bot, QQLocalChatID(id), context) {

    override suspend fun sendMessage(content: MessageContent): MessageRef? {
        val client = bot.client
        return bot.post(MessagePreSendEvent(bot, this, content.simplest))?.let {
            val messageRandom = Random.Default.nextInt()
            val response = client.sendAndWait(
                SendMessageRequest.create(
                    client,
                    routingHeader = routingHeader,
                    elements = client.messageEncoders.encode(this, it.content),
                    messageRandom = messageRandom
                )
            ) as SendMessageResponse
            if (response.errorMessage != null) {
                throw IllegalStateException(response.errorMessage)
            }
            if (context is QQGroup) {
                val group = context
                for (i in 0..3) {
                    val message =
                        group.pullHistoryMessages(group.lastReadSequence.get() - 10, group.lastReadSequence.get() + 1)
                            .find { message -> message.messageRandom == messageRandom }
                    if (message != null) {
                        return@let message.ref
                    }
                    delay(200)
                }
                throw IllegalStateException("Unable to pull back message, messageRandom=$messageRandom")
            }
            val message = QQMessage(bot, this@QQChat, bot.selfInfo, it.content, System.currentTimeMillis(), 0, 0, 0)
            message.ref
        }
    }

    override suspend fun removeMessage(ref: MessageRef) = if (this.contextContact != null) {
        TODO()
    } else {
        val message = ref.message!! as QQMessage
        (context as QQGroup).recallMessage(message.sequence, message.type)
    }

    suspend fun uploadImage(data: ByteArray) = if (this.contextContact != null) {
        (context as QQContact).uploadImage(data)
    } else {
        (context as QQGroup).uploadImage(data)
    }

}