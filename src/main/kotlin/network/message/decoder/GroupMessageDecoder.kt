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

import katium.client.qq.message.QQMessage
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessages

object GroupMessageDecoder : MessageDecoder {

    override suspend fun decode(client: QQClient, message: PbMessages.Message): QQMessage {
        val group = client.bot.getGroup(message.header.groupInfo.groupCode).chat
        val sender = client.bot.getUser(message.header.fromUin)
        return QQMessage(
            bot = client.bot,
            context = group,
            content = client.messageParsers.parse(message),
            sender = sender,
            time = message.header.time * 1000L
        )
    }

}