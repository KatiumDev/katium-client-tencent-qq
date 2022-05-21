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
package katium.client.qq.network.packet.wtlogin

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.tlv.applyT119
import katium.client.qq.network.codec.tlv.readT119
import katium.client.qq.network.codec.tlv.readTlvMap
import katium.client.qq.network.codec.tlv.release
import katium.core.util.netty.readUByte
import katium.core.util.netty.toArray
import java.util.*
import kotlin.random.Random

class LoginResponse(client: QQClient, uin: Int, command: Short) : OicqPacket.Response.Simple(client, uin, command) {

    var success: Boolean = false
        private set
    var captchaImage: ByteBuf? = null
        private set
    var captchaSignature: ByteBuf? = null
        private set
    var verifyUrl: String? = null
        private set
    var smsPhone: String? = null
        private set
    var errorMessage: String? = null
        private set

    override fun readBody(input: ByteBuf) {
        input.run {
            skipBytes(2) // sub command
            val type = readUByte().toUInt()
            skipBytes(2)
            val tlv = readTlvMap(2, release = false)
            if (0x402 in tlv) {
                client.sig.dpwd = Random.Default.nextBytes(16).toUByteArray()
                client.sig.t402 = tlv[0x402]!!.toArray(release = false).toUByteArray()
                @Suppress("DEPRECATION")
                client.sig.g =
                    Hashing.md5()
                        .hashBytes(client.deviceInfo.guid + client.sig.dpwd!!.toByteArray() + client.sig.t402!!.toByteArray())
                        .asBytes().toUByteArray()
            }
            when (type) {
                0x00u -> { // Login successful
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
                    success = true
                }
                0x01u -> errorMessage = "Wrong password"
                0x02u -> TODO("Need captcha")
                0x06u -> errorMessage = "Slider ticket error(6), network environment error, not allowed to login"
                0x09u -> errorMessage = "Protocol error(9)"
                0x28u -> errorMessage = "Account frozen(40)"
                0xA0u, 0xEFu -> TODO("Unsafe device")
                0xA1u, 0xA2u -> errorMessage = "Too many SMS requests($type)"
                0xA3u -> errorMessage = "Wrong device lock verification code(163)"
                0xB4u -> errorMessage = "Fallback(180), ECDH error"
                0xCCu -> TODO("Device lock layer-2")
                0xEDu -> errorMessage = "Account not enabled(237)"
                else -> throw IllegalStateException("Unknown login response type: $type")
            }
            tlv.release()
        }
    }

    override fun close() {
        captchaImage?.release()
        captchaSignature?.release()
    }

    override fun toString() =
        "LoginResponse(success=$success, captchaImage=$captchaImage, captchaSignature=$captchaSignature, verifyUrl=$verifyUrl, smsPhone=$smsPhone, errorMessage=$errorMessage)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoginResponse) return false
        if (success != other.success) return false
        if (captchaImage != other.captchaImage) return false
        if (captchaSignature != other.captchaSignature) return false
        if (verifyUrl != other.verifyUrl) return false
        if (smsPhone != other.smsPhone) return false
        if (errorMessage != other.errorMessage) return false
        return true
    }

    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + (captchaImage?.hashCode() ?: 0)
        result = 31 * result + (captchaSignature?.hashCode() ?: 0)
        result = 31 * result + (verifyUrl?.hashCode() ?: 0)
        result = 31 * result + (smsPhone?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }

}
