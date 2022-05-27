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

import katium.client.qq.QQLocalChatID
import katium.core.Bot
import katium.core.chat.Chat
import katium.core.chat.Chattable
import katium.core.chat.LocalChatID
import katium.core.message.MessageRef
import katium.core.message.content.MessageContent
import katium.core.user.User

class QQChat(bot: Bot, id: Long, context: Chattable) : Chat(bot, QQLocalChatID(id), context) {

    override val members: Set<User>
        get() = TODO("Not yet implemented")

    override fun removeMessage(message: MessageRef) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: MessageContent): MessageRef {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = "Unknown"

}