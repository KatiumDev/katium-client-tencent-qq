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

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.auth.LoginType
import katium.client.qq.network.crypto.tea.QQTeaCipher
import katium.client.qq.network.codec.base.writeQQShortLengthString
import katium.core.util.netty.buffer
import katium.core.util.netty.toArray
import kotlin.random.Random

fun ByteBuf.writeT106(
    uin: Long,
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
        writeInt(if (salt == 0) uin.toInt() else salt)
    }.toArray(true)).asBytes().toUByteArray()
    writeBytes(QQTeaCipher(key).encrypt(alloc().buffer {
        writeShort(4) // tgtgt version
        writeInt(Random.Default.nextInt())
        writeInt(ssoVersion)
        writeInt(appID) // fake app id
        writeInt(appClientVersion) // app client version
        writeLong((if (uin == 0L) salt else uin).toLong())
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