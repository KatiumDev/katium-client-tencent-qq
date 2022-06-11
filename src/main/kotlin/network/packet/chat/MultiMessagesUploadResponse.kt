package katium.client.qq.network.packet.chat

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.highway.Highway
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMultiMessages
import katium.core.util.netty.toArray
import java.net.InetSocketAddress

class MultiMessagesUploadResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: PbMultiMessages.MultiMessagesResponse
        private set

    lateinit var result: PbMultiMessages.MultiMessagesUploadResponse
        private set

    lateinit var uploadServers: Collection<InetSocketAddress>
        private set

    override fun readBody(input: ByteBuf) {
        response = PbMultiMessages.MultiMessagesResponse.parseFrom(input.toArray(release = false))

        if (response.uploadCount != 1) {
            throw IllegalStateException("Wrong multi messages upload response size: ${response.uploadCount}")
        } else {
            val result = response.getUpload(0)
            when (result.result) {
                0 -> this.result = result
                193 -> throw UnsupportedOperationException("Multi message too large(193)")
                else -> throw IllegalStateException("Unknown multi messages upload response result: ${result.result}")
            }
        }
        uploadServers = result.ipList.mapIndexed { index, ip ->
            InetSocketAddress(
                Highway.decodeIPv4(ip),
                result.getPort(index)
            )
        } + result.ipv6List.mapIndexed { index, ip ->
            InetSocketAddress(ip.toStringUtf8(), result.getIpv6Port(index))
        }
    }

}