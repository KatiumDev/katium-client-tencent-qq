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
package katium.client.qq.user

import katium.client.qq.chat.QQChat
import katium.client.qq.network.pb.PbMessagePackets
import katium.core.chat.Chat
import katium.core.user.Contact

class QQContact(asUser: QQUser) : Contact(asUser) {

    override val bot by asUser::bot
    val id by asUser::id

    override val chat: Chat by lazy {
        QQChat(
            bot, id, this,
            PbMessagePackets.RoutingHeader.newBuilder().setFriend(PbMessagePackets.ToFriend.newBuilder().setToUin(id))
                .build()
        )
    }

}