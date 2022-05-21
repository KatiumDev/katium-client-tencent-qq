package katium.client.qq.network.codec.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.event.QQTransportDecodersInitializeEvent
import katium.client.qq.network.packet.profileSvc.PushGroupSystemMessagesPacket
import katium.core.util.event.post
import kotlinx.coroutines.runBlocking

class TransportPacketDecoder(val client: QQClient) : MessageToMessageDecoder<TransportPacket.Response>() {

    val decoders: Map<String, (QQClient, TransportPacket.Response.Buffered) -> TransportPacket.Response> by lazy {
        val decoders = mutableMapOf<String, (QQClient, TransportPacket.Response.Buffered) -> TransportPacket.Response>()
        registerBuiltinDecoders(decoders)
        runBlocking {
            client.bot.post(QQTransportDecodersInitializeEvent(client, decoders))
        }
        decoders.toMap()
    }

    private fun registerBuiltinDecoders(decoders: MutableMap<String, (QQClient, TransportPacket.Response.Buffered) -> TransportPacket.Response>) {
        decoders["ProfileService.Pb.ReqSystemMsgNew.Group"] = ::PushGroupSystemMessagesPacket
    }

    override fun decode(ctx: ChannelHandlerContext, msg: TransportPacket.Response, out: MutableList<Any>) {
        out.add(
            if (msg is TransportPacket.Response.Buffered) {
                decoders[msg.command]?.invoke(client, msg) ?: msg
            } else msg
        )
    }

}