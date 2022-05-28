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
package katium.client.qq.network.codec.base

import io.netty.buffer.ByteBuf
import katium.core.util.netty.writeUBytes

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
    writeUBytes(data)
    return this
}

inline fun <T> ByteBuf.readWithIntLength(crossinline reader: ByteBuf.() -> T): T {
    val length = readInt()
    val result = slice(readerIndex(), readerIndex() + length).reader()
    skipBytes(length)
    return result
}
