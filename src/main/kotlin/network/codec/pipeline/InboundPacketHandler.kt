/*
 * Copyright 2022 Katium Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package katium.client.qq.network.codec.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.core.util.event.post
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

class InboundPacketHandler(val client: QQClient) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        super.channelRead(ctx, msg)
        msg as TransportPacket.Response
        println("recv ${msg.command}, ${msg.sequenceID}, ${msg.type}, ${msg.encryptType}")
        val handler = client.packetHandlers[msg.sequenceID]
        if (handler != null) {
            handler.resume(msg)
            client.packetHandlers.remove(msg.sequenceID)
            msg.close()
        } else {
            client.bot.launch {
                client.bot.post(QQPacketReceivedEvent(client, msg))
                msg.close()
            }
        }
    }

}