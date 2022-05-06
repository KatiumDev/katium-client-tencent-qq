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
import katium.client.qq.network.codec.auth.NetworkType
import kotlin.math.min

fun ByteBuf.writeT124(
    osType: ByteArray,
    osVersion: ByteArray,
    networkType: NetworkType = NetworkType.WIFI,
    simInfo: ByteArray,
    address: ByteArray = ByteArray(0),
    apn: ByteArray
) = writeTlv(0x124) {
    run {
        val length = min(osType.size, 16)
        writeShort(length)
        writeBytes(osType, 0, length)
    }
    run {
        val length = min(osVersion.size, 16)
        writeShort(length)
        writeBytes(osVersion, 0, length)
    }
    writeShort(networkType.value)
    run {
        val length = min(simInfo.size, 16)
        writeShort(length)
        writeBytes(simInfo, 0, length)
    }
    run {
        val length = min(address.size, 32)
        writeShort(length)
        writeBytes(address, 0, length)
    }
    run {
        val length = min(apn.size, 16)
        writeShort(length)
        writeBytes(apn, 0, length)
    }
}