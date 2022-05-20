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
import katium.client.qq.network.codec.base.writeQQShortLengthString

fun ByteBuf.writeT511(
    domains: Array<String> = arrayOf(
        "tenpay.com", "openmobile.qq.com", "docs.qq.com", "connect.qq.com",
        "qzone.qq.com", "vip.qq.com", "gamecenter.qq.com", "qun.qq.com", "game.qq.com",
        "qqweb.qq.com", "office.qq.com", "ti.qq.com", "mail.qq.com", "mma.qq.com",
    )
) = writeTlv(0x511) {
    val list = domains.filter { it.isNotEmpty() }
    writeShort(list.size)
    list.forEach { domain ->
        if (domain.startsWith('(') && domain.contains(')')) {
            val flagContent = domain.substring(1, domain.indexOf(')')).toInt()
            var flag = 0
            if ((flagContent and 0x100000 > 0)) {
                flag = flag or 1
            }
            if (flagContent and 0x8000000 > 0) {
                flag = flag or 2
            }
            writeByte(flag)
        } else {
            writeByte(1)
        }
        writeQQShortLengthString(domain.substring(domain.indexOf(')') + 1))
    }
}