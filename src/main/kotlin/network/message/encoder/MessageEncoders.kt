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
import katium.client.qq.message.content.QQService
import katium.client.qq.network.QQClient
import katium.client.qq.network.event.QQMessageEncodersInitializeEvent
import katium.core.message.content.*
import katium.core.util.CoroutineLazy
import katium.core.util.event.post
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

class MessageEncoders(val client: QQClient) {

    val encoders = CoroutineLazy(client) {
        val encoders = mutableMapOf<KClass<out MessageContent>, MessageEncoder<*>>()
        registerBuiltinEncoders(encoders)
        client.bot.post(QQMessageEncodersInitializeEvent(client, encoders))
        encoders.toMap()
    }

    private fun registerBuiltinEncoders(encoders: MutableMap<KClass<out MessageContent>, MessageEncoder<*>>) {
        encoders[MessageChain::class] = MessageChainEncoder
        encoders[PlainText::class] = PlainTextEncoder
        encoders[Image::class] = ImageEncoder
        encoders[QQService::class] = QQServiceEncoder
        encoders[At::class] = AtEncoder
        encoders[AtAll::class] = AtAllEncoder
        encoders[RefMessage::class] = RefMessageEncoder
        encoders[Forward::class] = ForwardEncoder
    }

    fun find(type: KClass<out MessageContent>): Pair<KClass<out MessageContent>, MessageEncoder<*>>? {
        val encoder = encoders.getSync()[type]
        return if (encoder != null) {
            type to encoder
        } else {
            type.superclasses.filter { it.isSubclassOf(MessageContent::class) }.map {
                @Suppress("UNCHECKED_CAST") find(it as KClass<out MessageContent>)
            }.firstOrNull()
        }
    }

    operator fun get(type: KClass<out MessageContent>) = find(type)?.second

    @Suppress("UNCHECKED_CAST")
    operator fun get(content: MessageContent) =
        (this[content::class] ?: FallbackEncoder) as MessageEncoder<MessageContent>

    fun shouldStandalone(content: MessageContent) = get(content).shouldStandalone

    fun getPriority(content: MessageContent) = get(content).priority

    suspend fun encode(
        chat: QQChat, content: MessageContent, withGeneralFlags: Boolean = false, isStandalone: Boolean = false
    ) = this[content].encode(
        client, chat, content, withGeneralFlags, isStandalone
    ).run {
        if (withGeneralFlags) {
            sortedBy { if (it.generalFlags != null) 0 else 1 }
        } else {
            filter { it.generalFlags == null }
        }
    }

}