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
package katium.client.qq.network.codec.tlv

import io.netty.buffer.ByteBuf

fun ByteBuf.writeT1(uin: Int, ip: ByteArray) = writeTlv(0x01) {
    if(ip.size != 4) throw IllegalArgumentException("IP must be 4 bytes")
    writeShort(1) // IP version
    writeInt(kotlin.random.Random.Default.nextInt())
    writeInt(uin)
    writeInt((System.currentTimeMillis() / 1000L).toInt())
    writeBytes(ip)
    writeShort(0)
}