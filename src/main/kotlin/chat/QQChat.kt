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
import kotlin.math.abs
import kotlin.random.Random

class QQChat(override val bot: QQBot, id: Long, context: ChatInfo, val routingHeader: PbMessagePackets.RoutingHeader) :
    Chat(bot, QQLocalChatID(id), context) {

    override suspend fun sendMessage(content: MessageContent): MessageRef? {
        val client = bot.client
        return bot.post(MessagePreSendEvent(bot, this, content.simplest))?.let {
            val isGroup = context is QQGroup
            val messageSequence =
                if (isGroup) client.allocGroupMessageSequenceID()
                else client.allocFriendMessageSequenceID()
            val messageRandom = Random.Default.nextInt()
            val time = System.currentTimeMillis() / 1000
            println(client.messageEncoders.encode(this, it.content, withGeneralFlags = isGroup))
            val response = client.sendAndWait(
                SendMessageRequest.create(
                    client,
                    messageSequence = messageSequence,
                    routingHeader = routingHeader,
                    elements = client.messageEncoders.encode(this, it.content, withGeneralFlags = isGroup),
                    messageRandom = messageRandom,
                    syncCookieTime = if (isGroup) null else time
                )
            ) as SendMessageResponse
            if (response.errorMessage != null) {
                throw IllegalStateException(response.errorMessage)
            }
            if (isGroup) {
                val group = context as QQGroup
                for (i in 0..14) {
                    val message =
                        group.pullHistoryMessages(group.lastReadSequence.get() - 10, group.lastReadSequence.get() + 1)
                            .find { message -> message.messageRandom == messageRandom }
                    if (message != null) {
                        return@let message.ref
                    }
                    delay(100L * i)
                }
                client.logger.error("Unable to pull back message, sequence=$messageSequence, random=$messageRandom, content=$content")
            }
            val message = QQMessage(
                bot, this@QQChat, bot.selfInfo, it.content,
                time = time * 1000, sequence = messageSequence, messageRandom = messageRandom
            )
            message.ref
        }
    }

    override suspend fun removeMessage(ref: MessageRef) = if (this.contextContact != null) {
        val message = ref.message!! as QQMessage
        (context as QQContact).recallMessage(message.sequence, message.messageRandom, message.time / 1000)
    } else {
        val message = ref.message!! as QQMessage
        (context as QQGroup).recallMessage(message.sequence, message.messageRandom)
    }

    suspend fun uploadImage(data: ByteArray) = if (this.contextContact != null) {
        (context as QQContact).uploadImage(data)
    } else {
        (context as QQGroup).uploadImage(data)
    }

}