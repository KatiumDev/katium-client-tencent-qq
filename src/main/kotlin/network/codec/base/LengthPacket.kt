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
package katium.client.qq.network.codec.base

import io.netty.buffer.ByteBuf
import katium.core.util.netty.writeUByteArray

/**
 * Write a packet with an int32 length prefix(without the length itself)
 */
inline fun ByteBuf.writeWithIntLength(crossinline writer: ByteBuf.() -> Unit): ByteBuf {
    val pos = writerIndex()
    writeZero(4)
    writer()
    setInt(pos, writerIndex() - pos)
    return this
}

fun ByteBuf.writeWithIntLength(data: ByteBuf): ByteBuf {
    writeInt(data.readableBytes() + 4)
    writeBytes(data)
    return this
}

fun ByteBuf.writeWithIntLength(data: ByteArray): ByteBuf {
    writeInt(data.size + 4)
    writeBytes(data)
    return this
}

fun ByteBuf.writeWithIntLength(data: UByteArray): ByteBuf {
    writeInt(data.size + 4)
    writeUByteArray(data)
    return this
}

inline fun <T> ByteBuf.readWithIntLength(crossinline reader: ByteBuf.() -> T): T {
    val length = readInt()
    val result = slice(readerIndex(), readerIndex() + length).reader()
    skipBytes(length)
    return result
}
