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

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.packet.readPacket
import kotlin.math.min

class ResponsePacketDecoder(val client: QQClient) : ByteToMessageDecoder() {

    var lastPacket: Pair<CompositeByteBuf, Int>? = null

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        var packet: TransportPacket.Response? = null
        if (lastPacket == null && `in`.readableBytes() >= 4) { // read new packet
            val size = `in`.readInt() - 4
            if (`in`.readableBytes() >= size) {
                packet = `in`.readSlice(size).readPacket(client, release = false)
            } else {
                lastPacket = `in`.alloc().compositeBuffer() to size
            }
        } else if (lastPacket != null) { // continue reading
            var (buffer, size) = lastPacket!!
            val readingSize = min(`in`.readableBytes(), size)
            size -= readingSize
            buffer.addComponent(true, `in`.readBytes(readingSize))
            if (size <= 0) {
                lastPacket = null
                packet = buffer.readPacket(client)
            } else {
                lastPacket = buffer to size
            }
        }
        if(packet != null) {
            out.add(packet)
        }
    }

}