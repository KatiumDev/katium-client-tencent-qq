package katium.client.qq.network.event

import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessages
import katium.core.event.BotEvent

class QQReceivedRawMessageEvent(val client: QQClient, val message: PbMessages.Message) : BotEvent(client.bot) {

    operator fun component2() = client
    operator fun component3() = message

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQReceivedRawMessageEvent) return false
        if (!super.equals(other)) return false
        if (client != other.client) return false
        if (message != other.message) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }

    override fun toString() = "QQReceivedRawMessageEvent(bot=$bot, client=$client, message=$message)"

}