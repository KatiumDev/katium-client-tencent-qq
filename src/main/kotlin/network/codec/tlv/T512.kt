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
import katium.client.qq.network.codec.base.readQQShortLengthString

/**
 * @return (psKeyMap, pt4TokenMap)
 */
fun ByteBuf.readT512(release: Boolean = true): Pair<Map<String, ByteArray>, Map<String, ByteArray>> {
    val length = readShort()
    val psKeyMap = mutableMapOf<String, ByteArray>()
    val pt4TokenMap = mutableMapOf<String, ByteArray>()
    for (i in 0 until length) {
        val domain = readQQShortLengthString()
        val psKey = readQQShortLengthString().toByteArray()
        val pt4Token = readQQShortLengthString().toByteArray()
        if (psKey.isNotEmpty()) {
            psKeyMap[domain] = psKey
        }
        if (pt4Token.isNotEmpty()) {
            pt4TokenMap[domain] = pt4Token
        }
    }
    if (release) {
        release()
    }
    return psKeyMap.toMap() to pt4TokenMap.toMap()
}