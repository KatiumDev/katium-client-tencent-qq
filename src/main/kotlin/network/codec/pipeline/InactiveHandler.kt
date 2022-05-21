package katium.client.qq.network.codec.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import katium.client.qq.network.QQClient
import kotlinx.coroutines.launch

class InactiveHandler(val client: QQClient) : ChannelInboundHandlerAdapter() {

    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        client.bot.launch {
            client.notifyOffline()
        }
    }

}