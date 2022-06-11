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
package katium.client.qq.network.codec.tlv

import io.netty.buffer.ByteBuf
import katium.core.util.netty.readUShort
import katium.core.util.netty.toArray
import java.util.*

class TlvMap : HashMap<Short, ByteBuf>, AutoCloseable {

    constructor(initialCapacity: Int, loadFactor: Float) : super(initialCapacity, loadFactor)
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(m: MutableMap<out Short, out ByteBuf>) : super(m)

    fun release() {
        values.forEach(ByteBuf::release)
    }

    override fun close() = release()

    override fun toString() = "{${
        entries.map { it.key to HexFormat.of().formatHex(it.value.duplicate().toArray(false)).uppercase() }
            .joinToString(", ") { (key, value) -> "$key: $value" }
    }}"

}

fun ByteBuf.readTlvMap(tagSize: Int = 2, release: Boolean = true): TlvMap {
    val map = mutableMapOf<Short, ByteBuf>()
    while (readableBytes() >= tagSize) {
        val k = when (tagSize) {
            1 -> readByte().toInt()
            2 -> readShort().toInt()
            4 -> readInt()
            else -> throw UnsupportedOperationException("Unsupported tag size: $tagSize")
        }
        if (k == 255) {
            break
        } else {
            map[k.toShort()] = readBytes(readUShort().toInt())
        }
    }
    if (release) {
        release()
    }
    return TlvMap(map)
}

fun ByteBuf.writeTlvMap(tlv: TlvMap, tagSize: Int = 2, release: Boolean = true): ByteBuf {
    when (tagSize) {
        1 -> writeByte(tlv.size)
        2 -> writeShort(tlv.size)
        4 -> writeInt(tlv.size)
        else -> throw UnsupportedOperationException("Unsupported tag size: $tagSize")
    }
    with(TlvWriterContext.IGNORE) {
        tlv.forEach { (type, data) ->
            writeTlv(type.toInt()) { writeBytes(data) }
        }
    }
    if (release) tlv.release()
    return this
}

inline fun ByteBuf.writeTlvMap(
    tagSize: Int = 2, crossinline writer: context(TlvWriterContext, ByteBuf) () -> Unit
): ByteBuf {
    val sizeOffset = writerIndex()
    writeZero(tagSize)
    val context = TlvWriterContext()
    writer(context, this)
    when (tagSize) {
        1 -> setByte(sizeOffset, context.tlvCount.value)
        2 -> setShort(sizeOffset, context.tlvCount.value)
        4 -> setIndex(sizeOffset, context.tlvCount.value)
        else -> throw UnsupportedOperationException("Unsupported tag size: $tagSize")
    }
    return this
}
