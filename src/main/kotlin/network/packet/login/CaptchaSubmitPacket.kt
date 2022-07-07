package katium.client.qq.network.packet.login

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.tlv.*

class CaptchaSubmitPacket(client: QQClient, val ticket: String) : OicqPacket.Request.Simple(
    client = client,
    uin = client.uin.toInt(),
    command = 0x0810,
    encryption = OicqPacket.EncryptType.ECDH,
    subCommand = 2,
) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID(), ticket: String) =
            TransportPacket.Request.Oicq(
                client = client,
                type = TransportPacket.Type.LOGIN,
                encryptType = TransportPacket.EncryptType.EMPTY_KEY,
                sequenceID = sequenceID,
                command = "wtlogin.login",
                packet = CaptchaSubmitPacket(client, ticket)
            )

    }

    override fun writeBody(output: ByteBuf) {
        output.writeTlvMap {
            writeT193(ticket = ticket)
            writeT8()
            writeT104(t104 = client.sig.t104)
            writeT116(miscBitmap = client.version.miscBitMap, subSigMap = client.version.subSigMap)
            if (client.sig.t547.isNotEmpty()) writeT547(t547 = client.sig.t547)
        }
    }

}