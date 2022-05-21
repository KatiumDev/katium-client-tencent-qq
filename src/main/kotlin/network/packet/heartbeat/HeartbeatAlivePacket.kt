package katium.client.qq.network.packet.heartbeat

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket

class HeartbeatAlivePacket private constructor(client: QQClient, sequenceID: Int) : TransportPacket.Request.Simple(
    client = client,
    type = TransportPacket.Type.LOGIN,
    encryptType = TransportPacket.EncryptType.NONE,
    sequenceID = sequenceID,
    command = "Heartbeat.Alive"
) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocSequenceID()) =
            HeartbeatAlivePacket(client, sequenceID)

    }

    override fun writeBody(output: ByteBuf) {
    }

}