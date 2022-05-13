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
import katium.client.qq.network.codec.packet.wtlogin.LoginExtraData
import katium.client.qq.network.codec.packet.wtlogin.writeLoginExtraData

fun ByteBuf.writeT536(loginExtraData: Array<LoginExtraData>)  = writeTlv(0x536) {
    writeByte(1)
    writeByte(loginExtraData.size)
    for (extraData in loginExtraData) {
        writeLoginExtraData(extraData)
    }
}