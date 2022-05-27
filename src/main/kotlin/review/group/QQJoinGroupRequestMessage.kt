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
package katium.client.qq.review.group

import katium.client.qq.QQLocalChatID
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbSystemMessages
import katium.core.chat.ChatInfo
import katium.core.review.group.JoinGroupRequestMessage
import katium.core.user.User

class QQJoinGroupRequestMessage(
    chatInfo: ChatInfo,
    val sequence: Long,
    override val message: String?,
    override val requester: User,
    override val processed: Boolean,
    override val suspicious: Boolean,
) : JoinGroupRequestMessage(chatInfo) {

    constructor(client: QQClient, message: PbSystemMessages.StructMessage) : this(
        chatInfo = client.bot.getGroup(QQLocalChatID(message.message.groupCode)),
        sequence = message.messageSequence,
        message = message.message.addition,
        requester = client.bot.getUser(QQLocalChatID(message.requesterUin)),
        processed = message.message.subType == 2,
        suspicious = message.message.warningTips.size() > 0
    )

    override val id: String = sequence.toString()

}