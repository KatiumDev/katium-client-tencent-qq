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
package katium.client.qq.network.codec.struct.tlv

import io.netty.buffer.ByteBuf
import katium.core.util.netty.readUShort

/**
 * https://cs.github.com/Mrs4s/MiraiGo/tree/master/internal/tlv
 * https://cs.github.com/lz1998/rs-qq/blob/master/rq-engine/src/command/wtlogin/tlv_writer.rs
 * https://github.com/takayama-lily/oicq/blob/main/lib/core/tlv.ts
 * https://github.com/mamoe/mirai/blob/dev/mirai-core/src/commonMain/kotlin/network/protocol/packet/Tlv.kt
 */
inline fun ByteBuf.writeTlv(command: Int, crossinline writer: ByteBuf.() -> Unit): ByteBuf {
    writeShort(command)
    writeZero(2)
    val pos = writerIndex()
    writer()
    setInt(pos - 2, writerIndex() - pos)
    return this
}

inline fun <T> ByteBuf.readTlv(release: Boolean, crossinline reader: ByteBuf.() -> T): T {
    skipBytes(2)
    val result = reader()
    if (release) {
        release()
    }
    return result
}

/**
 * https://github.com/mamoe/mirai/blob/dev/mirai-core/src/commonMain/kotlin/network/protocol/packet/Tlv.kt#L464
 */
internal const val GUID_FLAG: Long = (1 shl 24 and -0x1000000) or (0 shl 8 and 0xFF00)

typealias TlvMap = Map<Short, ByteBuf>

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
    return map.toMap()
}

fun TlvMap.release() {
    values.forEach(ByteBuf::release)
}

inline fun TlvMap.use(crossinline block: TlvMap.() -> Unit) {
    block()
    release()
}
