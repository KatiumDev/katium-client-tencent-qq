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
package katium.client.qq.message

import katium.client.qq.QQBot
import katium.core.chat.Chat
import katium.core.chat.ChatInfo
import katium.core.message.Message
import katium.core.message.MessageRef
import katium.core.message.content.MessageContent

class QQMessage(
    bot: QQBot, context: Chat, sender: ChatInfo, content: MessageContent, time: Long,
    val sequence: Int, val messageRandom: Int
) : Message(bot, context, sender, content, time) {

    override val ref: MessageRef by lazy { QQMessageRef(bot, this, sequence) }

}