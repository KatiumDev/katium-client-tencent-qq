package katium.client.qq.network.event

import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.core.event.BotEvent

class QQTransportDecodersInitializeEvent(
    val client: QQClient,
    val decoders: MutableMap<String, (QQClient, TransportPacket.Response.Buffered) -> TransportPacket.Response>
) : BotEvent(client.bot) {

    operator fun component2() = client
    operator fun component3() = decoders

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQTransportDecodersInitializeEvent) return false
        if (!super.equals(other)) return false
        if (client != other.client) return false
        if (decoders != other.decoders) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + decoders.hashCode()
        return result
    }

    override fun toString() = "QQTransportDecodersInitializeEvent(bot=$bot, client=$client, decoders=$decoders)"

}