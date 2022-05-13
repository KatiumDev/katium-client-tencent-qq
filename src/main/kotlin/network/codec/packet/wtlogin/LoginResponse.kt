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
package katium.client.qq.network.codec.packet.wtlogin

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.struct.tlv.applyT119
import katium.client.qq.network.codec.struct.tlv.readT119
import katium.client.qq.network.codec.struct.tlv.readTlvMap
import katium.client.qq.network.codec.struct.tlv.release
import katium.core.util.netty.toArray
import java.util.HexFormat
import kotlin.random.Random

data class LoginResponse(
    val success: Boolean,
    val captchaImage: ByteBuf? = null,
    val captchaSignature: ByteBuf? = null,
    val verifyUrl: String? = null,
    val smsPhone: String? = null,
    val errorMessage: String? = null,
) : AutoCloseable {

    override fun close() {
        captchaImage?.release()
        captchaSignature?.release()
    }

}

fun ByteBuf.readLoginResponse(client: QQClient, release: Boolean = true): LoginResponse {
    println(HexFormat.of().formatHex(duplicate().toArray(false)).uppercase())
    skipBytes(2) // sub command
    val t = readByte().toInt()
    skipBytes(2)
    val tlv = readTlvMap(2)
    if (0x402 in tlv) {
        client.sig.dpwd = Random.Default.nextBytes(16).toUByteArray()
        client.sig.t402 = tlv[0x402]!!.toArray(release = false).toUByteArray()
        @Suppress("DEPRECATION")
        client.sig.g =
            Hashing.md5()
                .hashBytes(client.deviceInfo.guid + client.sig.dpwd!!.toByteArray() + client.sig.t402!!.toByteArray())
                .asBytes().toUByteArray()
    }
    val response = when (t) {
        0x01 -> { // Login successful
            /*if(0x150 in tlv) {
                client.sig.t150 = tlv[0x150]!!.toUByteArray()
            }
            if(0x161 in tlv) {
                val tlv2 = alloc().buffer(tlv[0x161]!!).skipBytes(2).readTlvMap(2)
                if(0x172 in tlv2) {
                    client.sig.rollbackSig = tlv2[0x172]!!.toUByteArray()
                }
            }*/
            if (0x403 in tlv) {
                client.sig.randomSeed = tlv[0x403]!!.toArray(release = false).toUByteArray()
            }
            client.applyT119(tlv[0x119]!!.readT119(client.deviceInfo.tgtgtKey, release = false))
            LoginResponse(success = true)
        }
        else -> throw IllegalStateException("Unknown login response type: $t")
    }
    tlv.release()
    if (release) {
        release()
    }
    return response
}
