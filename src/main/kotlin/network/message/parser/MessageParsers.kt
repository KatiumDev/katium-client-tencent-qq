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
package katium.client.qq.network.message.parser

import katium.client.qq.network.QQClient
import katium.client.qq.network.event.QQMessageParsersInitializeEvent
import katium.client.qq.network.pb.PbMessages
import katium.core.message.content.MessageChain
import katium.core.util.event.post
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking

class MessageParsers(val client: QQClient) {

    val parsers: Map<Int, MessageParser> by lazy {
        val parsers = mutableMapOf<Int, MessageParser>()
        registerBuiltinParsers(parsers)
        runBlocking(CoroutineName("Initialize Message Parsers")) {
            client.bot.post(QQMessageParsersInitializeEvent(client, parsers))
        }
        parsers.toMap()
    }

    private fun registerBuiltinParsers(parsers: MutableMap<Int, MessageParser>) {
        parsers[1] = PlainTextParser
    }

    operator fun get(type: Int) = parsers[type]

    operator fun get(element: PbMessages.Element): MessageParser? {
        val fieldKeys = element.allFields.keys
        if (fieldKeys.isEmpty()) return null
        if (fieldKeys.size != 1) throw UnsupportedOperationException("Too many fields: $fieldKeys in $element")
        return (get(fieldKeys.first().number)
            ?: throw UnsupportedOperationException("Unknown element type: ${fieldKeys.first()}(${fieldKeys.first().number})"))
    }

    fun parse(message: PbMessages.Message) = MessageChain(*message.body.richText.elementsList.map {
        get(it)?.parse(client, message, it)
    }.filterNotNull().toTypedArray()).simplest

}