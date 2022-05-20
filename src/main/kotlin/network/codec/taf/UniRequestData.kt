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
package katium.client.qq.network.codec.taf

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.core.util.netty.buffer

fun ByteBuf.wrapUniRequestData(release: Boolean = true): ByteBuf = ByteBufAllocator.DEFAULT.buffer {
    writeByte(0x0A)
    writeBytes(this@wrapUniRequestData)
    writeByte(0x0B)
    if (release) this@wrapUniRequestData.release()
}

/**
 * Decode a UniRequestData.
 *
 * The reference counter of the ByteBuf is not changed.
 */
fun ByteBuf.decodeUniRequestData(size : Int = readableBytes() - 2): ByteBuf {
    skipBytes(1)
    val data = readSlice(size)
    skipBytes(1)
    return data
}