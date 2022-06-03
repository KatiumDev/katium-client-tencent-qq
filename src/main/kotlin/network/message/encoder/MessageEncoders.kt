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
package katium.client.qq.network.message.encoder

import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQServiceMessage
import katium.client.qq.network.QQClient
import katium.client.qq.network.event.QQMessageEncodersInitializeEvent
import katium.client.qq.network.pb.PbMessageElements
import katium.core.message.content.Image
import katium.core.message.content.MessageChain
import katium.core.message.content.MessageContent
import katium.core.message.content.PlainText
import katium.core.util.event.post
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

class MessageEncoders(val client: QQClient) {

    val encoders: Map<KClass<out MessageContent>, MessageEncoder<*>> by lazy {
        val encoders = mutableMapOf<KClass<out MessageContent>, MessageEncoder<*>>()
        registerBuiltinEncoders(encoders)
        runBlocking(CoroutineName("Initialize Message Encoders")) {
            client.bot.post(QQMessageEncodersInitializeEvent(client, encoders))
        }
        encoders.toMap()
    }

    private fun registerBuiltinEncoders(encoders: MutableMap<KClass<out MessageContent>, MessageEncoder<*>>) {
        encoders[MessageChain::class] = MessageChainEncoder
        encoders[PlainText::class] = PlainTextEncoder
        encoders[Image::class] = ImageEncoder
        encoders[QQServiceMessage::class] = QQServiceMessageEncoder
    }

    operator fun get(type: KClass<out MessageContent>): MessageEncoder<*>? =
        encoders[type] ?: type.superclasses.filter { it.isSubclassOf(MessageContent::class) }.map {
            @Suppress("UNCHECKED_CAST") get(it as KClass<out MessageContent>)
        }.firstOrNull()

    @Suppress("UNCHECKED_CAST")
    operator fun get(content: MessageContent) = (this[content::class]
        ?: throw UnsupportedOperationException("No message encoder for ${content::class}")) as MessageEncoder<MessageContent>

    suspend fun encode(chat: QQChat, content: MessageContent, withGeneralFlags: Boolean = false) =
        (this[content].encode(client, chat, content) + (if (withGeneralFlags) createGeneralFlags(chat, content).map {
            PbMessageElements.Element.newBuilder().setGeneralFlags(it).build()
        }.toTypedArray() else emptyArray())).toList()

    suspend fun createGeneralFlags(chat: QQChat, content: MessageContent) =
        this[content].createGeneralFlags(client, chat, content).toList()

}