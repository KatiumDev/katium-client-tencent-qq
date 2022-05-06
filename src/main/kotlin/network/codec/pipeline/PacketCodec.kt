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
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.struct.packet.Packet
import katium.client.qq.network.codec.struct.packet.writePacket

class PacketCodec(val client: QQClient) : MessageToByteEncoder<Packet>(Packet::class.java) {

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
        out.writePacket(client, msg)
    }

}