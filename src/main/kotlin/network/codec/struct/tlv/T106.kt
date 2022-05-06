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

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.codec.auth.LoginType
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.client.qq.network.codec.struct.writeQQShortLengthString
import katium.core.util.netty.buffer
import katium.core.util.netty.toArray
import kotlin.random.Random

fun ByteBuf.writeT106(
    uin: Int,
    salt: Int = 0,
    appID: Int = 16,
    subAppID: Int,
    ssoVersion: Int,
    appClientVersion: Int = 0,
    isSavePassword: Boolean = true,
    passwordMD5: ByteArray,
    guidAvailable: Boolean,
    guid: ByteArray?,
    tgtgtKey: ByteArray,
    loginType: LoginType = LoginType.PASSWORD,
    wtf: Int = 0
) = writeTlv(0x106) {
    @Suppress("DEPRECATION")
    val key = Hashing.md5().hashBytes(alloc().buffer {
        writeBytes(passwordMD5)
        writeInt(0) // byteArrayOf(0x00, 0x00, 0x00, 0x00)
        writeInt(if (salt == 0) uin else salt)
    }.toArray(true)).asBytes().toUByteArray()
    writeBytes(QQTeaCipher(key).encrypt(alloc().buffer {
        writeShort(4) // tgtgt version
        writeInt(Random.Default.nextInt())
        writeInt(ssoVersion)
        writeInt(appID) // fake app id
        writeInt(appClientVersion) // app client version
        writeLong((if (uin == 0) salt else uin).toLong())
        writeInt((System.currentTimeMillis() / 1000L).toInt())
        writeInt(0) // fake IP address({0x00, 0x00, 0x00, 0x00})
        writeBoolean(isSavePassword)
        writeBytes(passwordMD5)
        writeBytes(tgtgtKey)
        writeInt(wtf)
        writeBoolean(guidAvailable)
        writeBytes(guid ?: Random.Default.nextBytes(16))
        writeInt(subAppID)
        writeInt(loginType.value)
        writeQQShortLengthString(uin.toString())
        writeShort(0)
    }))
}