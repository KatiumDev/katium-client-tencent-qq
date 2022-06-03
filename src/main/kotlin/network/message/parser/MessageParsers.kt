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
import katium.core.util.event.post
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking

class MessageParsers(val client: QQClient) {

    val parsers: Map<Int, MessageParser> by lazy {
        val parsers = mutableMapOf<Int, MessageParser>()
        registerBuiltinDecoders(parsers)
        runBlocking(CoroutineName("Initialize Message Parsers")) {
            client.bot.post(QQMessageParsersInitializeEvent(client, parsers))
        }
        parsers.toMap()
    }

    private fun registerBuiltinDecoders(parsers: MutableMap<Int, MessageParser>) {
        parsers[9] = FriendMessageParser
        parsers[10] = FriendMessageParser
        parsers[31] = FriendMessageParser
        parsers[79] = FriendMessageParser
        parsers[97] = FriendMessageParser
        parsers[120] = FriendMessageParser
        parsers[132] = FriendMessageParser
        parsers[133] = FriendMessageParser
        parsers[166] = FriendMessageParser
        parsers[167] = FriendMessageParser
        parsers[82] = GroupMessageParser
    }

    operator fun get(type: Int) = parsers[type]

}