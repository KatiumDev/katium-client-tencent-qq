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

import katium.client.qq.chat.QQChat
import katium.client.qq.message.QQMessage
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessage

object FriendMessageParser : MessageParser {

    override suspend fun parse(client: QQClient, message: PbMessage): QQMessage {
        val sender = client.bot.getUser(message.header.fromUin)
        return QQMessage(
            bot = client.bot,
            context = sender.chat!!,
            content = client.messageDecoders.decode(sender.chat!! as QQChat, message),
            sender = sender,
            time = message.header.time * 1000L,
            sequence = message.header.sequence,
            messageRandom = message.body.richText.attributes.random
        )
    }

}