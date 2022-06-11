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
import katium.client.qq.network.codec.base.writeQQShortLengthString

context(TlvWriterContext) fun ByteBuf.writeT511(
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