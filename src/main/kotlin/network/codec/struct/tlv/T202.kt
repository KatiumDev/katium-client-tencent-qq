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
import kotlin.math.min

fun ByteBuf.writeT202(wifiBSSID: ByteArray, wifiSSID: ByteArray) = writeTlv(0x202) {
    run {
        val length = min(wifiBSSID.size, 16)
        writeShort(length)
        writeBytes(wifiBSSID, 0, length)
    }
    run {
        val length = min(wifiSSID.size, 32)
        writeShort(length)
        writeBytes(wifiSSID, 0, length)
    }
}