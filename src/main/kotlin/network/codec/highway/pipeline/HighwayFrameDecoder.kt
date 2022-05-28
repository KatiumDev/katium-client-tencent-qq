package katium.client.qq.network.codec.highway.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import katium.client.qq.network.pb.PbHighway

class HighwayFrameDecoder : ByteToMessageDecoder() {

    companion object {
        const val MAX_FRAME_SIZE = 1024 * 100 // 100k
    }

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        if (`in`.readableBytes() >= 9) {
            `in`.skipBytes(1)
            val headerSize = `in`.readInt()
            val bodySize = `in`.readInt()
            if (headerSize > MAX_FRAME_SIZE) {
                throw UnsupportedOperationException("Highway response header too big, headerSize: $headerSize")
            }
            if (bodySize > MAX_FRAME_SIZE) {
                throw UnsupportedOperationException("Highway response body too big, bodySize: $bodySize")
            }
            val header = ByteArray(headerSize)
            `in`.readBytes(header)
            val body = if (bodySize == 0) null else {
                val buffer = ByteArray(bodySize)
                `in`.readBytes(buffer)
                buffer.toUByteArray()
            }
            `in`.skipBytes(1)
            out.add(PbHighway.HighwayResponseHeader.parseFrom(header) to body)
        }
    }

}