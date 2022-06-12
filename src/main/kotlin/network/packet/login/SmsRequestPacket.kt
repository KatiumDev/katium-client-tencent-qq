package katium.client.qq.network.packet.login

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.tlv.*

class SmsRequestPacket(client: QQClient) : OicqPacket.Request.Simple(
    client = client,
    uin = client.uin.toInt(),
    command = 0x0810,
    encryption = OicqPacket.EncryptType.ECDH,
    subCommand = 8,
) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID()) = TransportPacket.Request.Oicq(
            client = client,
            type = TransportPacket.Type.LOGIN,
            encryptType = TransportPacket.EncryptType.EMPTY_KEY,
            sequenceID = sequenceID,
            command = "wtlogin.login",
            packet = SmsRequestPacket(client)
        )

    }

    override fun writeBody(output: ByteBuf) {
        output.writeTlvMap {
            writeT8()
            writeT104(t104 = client.sig.t104)
            writeT116(miscBitmap = client.version.miscBitMap, subSigMap = client.version.subSigMap)
            writeT174(t174 = client.sig.t174)
            writeT17A(9)
            writeT197()
        }
    }

}