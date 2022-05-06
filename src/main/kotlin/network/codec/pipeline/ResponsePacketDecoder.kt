/*
 * Katium Client Tencent QQ: Tencent QQ protocol implementation for Katium
 * Copyright (C) 2022  Katium Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package katium.client.qq.network.codec.pipeline

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.struct.packet.ResponsePacket
import katium.client.qq.network.codec.struct.packet.readPacket
import kotlin.math.min

class ResponsePacketDecoder(val client: QQClient) : ByteToMessageDecoder() {

    var lastPacket: Pair<CompositeByteBuf, Int>? = null

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        var packet: ResponsePacket? = null
        if (lastPacket == null && `in`.readableBytes() >= 4) { // read new packet
            val size = `in`.readInt() - 4
            if (`in`.readableBytes() >= size) {
                packet = `in`.readRetainedSlice(size).readPacket(client)
            } else {
                lastPacket = `in`.alloc().compositeBuffer().addComponent(`in`.readBytes(`in`.readableBytes())) to size
            }
        } else if (lastPacket != null) { // continue reading
            var (buf, size) = lastPacket!!
            val readingSize = min(`in`.readableBytes(), size)
            size -= readingSize
            buf.addComponent(`in`.readBytes(readingSize))
            if (size <= 0) {
                packet = buf.readPacket(client)
                lastPacket = null
            } else {
                lastPacket = buf to size
            }
        }
        if(packet != null) {
            out.add(packet)
        }
    }

}