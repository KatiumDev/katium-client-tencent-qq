package katium.client.qq.network.codec.highway.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import katium.client.qq.network.codec.highway.HighwayResponseFrame
import katium.client.qq.network.codec.highway.HighwaySession
import kotlin.coroutines.resume

class HighwayResponseHandler(val session: HighwaySession) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        super.channelRead(ctx, msg)
        @Suppress("UNCHECKED_CAST")
        (session.responseContinuation.getAndSet(null)
            ?: throw IllegalStateException("No response expected")).resume(msg as HighwayResponseFrame)
    }

}