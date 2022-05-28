package katium.client.qq.network.codec.highway.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import katium.client.qq.network.codec.highway.HighwayRequestFrame
import katium.client.qq.network.codec.highway.writeHighwayFrame

class HighwayFrameEncoder : MessageToByteEncoder<HighwayRequestFrame>() {

    override fun encode(ctx: ChannelHandlerContext, msg: HighwayRequestFrame, out: ByteBuf) {
        val (header, body) = msg
        out.writeHighwayFrame(header, body)
    }

}