package katium.client.qq.review.group

import katium.client.qq.QQLocalChatID
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbSystemMessage
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

    constructor(client: QQClient, message: PbSystemMessage.StructMessage) : this(
        chatInfo = client.bot.getGroup(QQLocalChatID(message.message.groupCode)),
        sequence = message.messageSequence,
        message = message.message.addition,
        requester = client.bot.getUser(QQLocalChatID(message.requesterUin)),
        processed = message.message.subType == 2,
        suspicious = message.message.warningTips.size() > 0
    )

    override val id: String = sequence.toString()

}