package katium.client.qq.group

import katium.client.qq.QQLocalChatID
import katium.core.Bot
import katium.core.chat.Chat
import katium.core.group.Group
import katium.core.user.User

class QQGroup(bot: Bot, val id: Long) : Group(bot, QQLocalChatID(id)) {

    override val chat: Chat
        get() = TODO("Not yet implemented")

    override val members: Set<User>
        get() = TODO("Not yet implemented")

    override val name: String
        get() = TODO("Not yet implemented")

}