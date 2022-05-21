package katium.client.qq.network.event

import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.core.event.BotEvent

class QQPacketReceivedEvent(val client: QQClient, val packet: TransportPacket.Response) : BotEvent(client.bot) {

    operator fun component2() = client
    operator fun component3() = packet

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQChannelInitializeEvent) return false
        if (!super.equals(other)) return false
        if (client != other.client) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + client.hashCode()
        return result
    }

    override fun toString() = "QQPacketReceivedEvent(bot=$bot, client$client)"

}