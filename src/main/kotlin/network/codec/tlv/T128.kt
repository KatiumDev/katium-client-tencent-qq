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
import kotlin.math.min

fun ByteBuf.writeT128(
    guidFromFileNull: Boolean = false,
    guidAvailable: Boolean = true,
    guidChanged: Boolean = false,
    guidFlag: Long = GUID_FLAG,
    buildModel: ByteArray,
    guid: ByteArray,
    buildBrand: ByteArray
) = writeTlv(0x128) {
    writeShort(0)
    writeBoolean(guidFromFileNull)
    writeBoolean(guidAvailable)
    writeBoolean(guidChanged)
    writeInt(guidFlag.toInt())
    run {
        val length = min(buildModel.size, 32)
        writeShort(length)
        writeBytes(buildModel, 0, length)
    }
    run {
        val length = min(guid.size, 16)
        writeShort(length)
        writeBytes(guid, 0, length)
    }
    run {
        val length = min(buildBrand.size, 16)
        writeShort(length)
        writeBytes(buildBrand, 0, length)
    }
}