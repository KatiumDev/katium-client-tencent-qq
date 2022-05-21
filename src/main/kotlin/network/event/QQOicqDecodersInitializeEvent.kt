package katium.client.qq.network.event

import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.oicq.OicqPacketCodec
import katium.client.qq.network.crypto.EncryptionMethod
import katium.core.event.BotEvent

class QQOicqDecodersInitializeEvent(
    val codec: OicqPacketCodec,
    val decoders: MutableMap<String, (QQClient, Int, Short) -> OicqPacket.Response.Simple>
) : BotEvent(codec.client.bot) {

    val client by codec::client

    fun component2() = codec
    fun component3() = decoders

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQOicqDecodersInitializeEvent) return false
        if (!super.equals(other)) return false
        if (codec != other.codec) return false
        if (decoders != other.decoders) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + codec.hashCode()
        result = 31 * result + decoders.hashCode()
        return result
    }

    override fun toString() = "QQOicqDecodersInitializeEvent(bot=$bot, codec=$codec, decoders=$decoders)"

}