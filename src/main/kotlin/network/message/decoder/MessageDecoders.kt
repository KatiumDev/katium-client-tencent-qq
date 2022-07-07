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
package katium.client.qq.network.message.decoder

import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.client.qq.network.event.QQMessageDecodersInitializeEvent
import katium.client.qq.network.message.pb.PbMessage
import katium.client.qq.network.message.pb.PbMessageElement
import katium.core.message.content.MessageChain
import katium.core.util.CoroutineLazy
import katium.core.util.event.post

class MessageDecoders(val client: QQClient) {

    val decoders = CoroutineLazy(client) {
        val decoders = mutableListOf<MessageDecoder<*>>()
        registerBuiltinParsers(decoders)
        client.bot.post(QQMessageDecodersInitializeEvent(client, decoders))
        decoders.reversed().toList()
    }

    private fun registerBuiltinParsers(decoders: MutableList<MessageDecoder<*>>) {
        decoders += FallbackDecoder
        decoders += PlainTextDecoder
        decoders += NotOnlineImageDecoder
        decoders += CustomFaceDecoder
        decoders += QQServiceDecoder
        decoders += RefMessageDecoder
    }

    operator fun get(type: Int) = decoders.getSync()[type]

    operator fun get(element: PbMessageElement): Pair<MessageDecoder<*>, Any>? {
        decoders.getSync().forEach {
            val selected = it.select(element)
            if (selected != null) return it to selected
        }
        throw UnsupportedOperationException("Unknown element: $element")
    }

    suspend fun decode(context: QQChat, message: PbMessage) = MessageChain(*message.body.richText.elements.mapNotNull {
        get(it)?.run {
            val (decoder, selected) = this
            @Suppress("UNCHECKED_CAST") (decoder as MessageDecoder<Any>).decode(client, context, message, selected)
        }
    }.toTypedArray()).simplest

}