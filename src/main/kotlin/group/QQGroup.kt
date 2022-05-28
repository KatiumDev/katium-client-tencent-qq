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
package katium.client.qq.group

import katium.client.qq.QQBot
import katium.client.qq.QQLocalChatID
import katium.client.qq.chat.QQChat
import katium.client.qq.network.pb.PbMessagePackets
import katium.core.chat.Chat
import katium.core.group.Group
import katium.core.user.User

class QQGroup(override val bot: QQBot, val id: Long) : Group(bot, QQLocalChatID(id)) {

    override val chat: Chat by lazy {
        QQChat(
            bot, id, this,
            PbMessagePackets.RoutingHeader.newBuilder().setGroup(PbMessagePackets.ToGroup.newBuilder().setGroupCode(id))
                .build()
        )
    }

    override val members: Set<User>
        get() = TODO("Not yet implemented")

    override val name: String
        get() = "Unknown"

}