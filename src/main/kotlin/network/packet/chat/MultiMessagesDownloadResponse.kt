package katium.client.qq.network.packet.chat

import io.netty.buffer.ByteBuf
import katium.client.qq.message.QQMessage
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.highway.Highway
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMultiMessages
import katium.client.qq.util.CoroutineLazy
import katium.core.util.netty.toArray
import java.net.InetSocketAddress

class MultiMessagesDownloadResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: PbMultiMessages.MultiMessagesResponse
        private set

    lateinit var result: PbMultiMessages.MultiMessagesDownloadResponse
        private set

    lateinit var messages: CoroutineLazy<List<QQMessage>>
        private set

    override fun readBody(input: ByteBuf) {
        response = PbMultiMessages.MultiMessagesResponse.parseFrom(input.toArray(release = false))

        if (response.downloadCount != 1) {
            throw IllegalStateException("Wrong multi messages download response size: ${response.downloadCount}")
        } else {
            val result = response.getDownload(0)
            when (result.result) {
                0 -> this.result = result
                193 -> throw UnsupportedOperationException("Multi message too large(193)")
                199 -> throw UnsupportedOperationException("Multi message wrong resourceID(199)")
                else -> throw IllegalStateException("Unknown multi messages download response result: ${result.result}")
            }
        }
        val serverAddress = if (!result.hasExtensionInfo() || result.extensionInfo.channelType != 2) {
            (result.ipList.mapIndexed { index, ip ->
                InetSocketAddress(
                    Highway.decodeIPv4(ip), result.getPort(index)
                )
            } + result.ipv6List.mapIndexed { index, ip ->
                InetSocketAddress(Highway.decodeIPv6(ip.toByteArray()), result.getIpv6Port(index))
            }).first().run {
                @Suppress("HttpUrlsUsage") "http://${address.hostAddress}:$port"
            }
        } else "https://ssl.htdata.qq.com"
        val url = serverAddress + result.thumbDownPara.toStringUtf8()
        messages = CoroutineLazy(client) {
            client.downloadMultiMessages(url, result.key.toByteArray().toUByteArray())
        }
    }

}