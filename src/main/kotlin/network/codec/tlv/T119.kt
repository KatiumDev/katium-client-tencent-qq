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

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.crypto.tea.QQTeaCipher
import katium.client.qq.network.crypto.tea.TeaCipher
import katium.core.util.netty.buffer
import katium.core.util.netty.toArray
import katium.core.util.netty.use

fun ByteBuf.readT119(key: ByteArray, release: Boolean = true): TlvMap =
    QQTeaCipher(key.toUByteArray()).decrypt(this, release = release).skipBytes(2).readTlvMap()

fun QQClient.applyT119(tlv: TlvMap) {
    /*if(0x130 in tlv) {
        applyT130(t130)
    }*/
    /*if(0x113 in tlv) {
        applyT130(0x113)
    }*/
    /*if(0x528 in tlv) {
        sig.t528 = tlv[0x528]!!.toArray(false).toUByteArray()
    }*/
    /*if(0x530 in tlv) {
        sig.t530 = tlv[0x530]!!.toArray(false).toUByteArray()
    }*/
    if (0x108 in tlv) {
        sig.ksid = tlv[0x108]!!.toArray(false).toUByteArray()
    }
    val (age, gender, nick) = tlv[0x11A]!!.readT11A(false)
    // @TODO: handle self summary card info
    /*if (0x125 in tlv) {
        val (openID, openKey) = tlv[0x125]!!.readT125(false)
    }*/
    /*if(0x186 in tlv) {
        applyT186(0x186)
    }*/
    /*if (0x199 in tlv) {
        val (openID, payToken) = tlv[0x199]!!.readT199(false)
    }*/
    /*if (0x200 in tlv) {
        val (pf, pfKey) = tlv[0x200]!!.readT200(false)
    }*/
    /*if (0x531 in tlv) {
        val (a1, noPicSig) = tlv[0x531]!!.readT531(false)
    }*/
    /*if (0x138 in tlv) {
        tlv[0x200]!!.readT138(false)
    }*/
    if (0x512 in tlv) {
        val (psKeyMap, pt4TokenMap) = tlv[0x512]!!.readT512(false)
        sig.psKeyMap = psKeyMap
        sig.pt4TokenMap = pt4TokenMap
    }
    if (0x134 in tlv) {
        oicqCodec.wtSessionTicketKeyCipher = QQTeaCipher(tlv[0x134]!!.toArray(false).toUByteArray())
    }
    sig.loginBitmap = 0uL
    if (0x16A in tlv) {
        sig.srmToken = tlv[0x16A]!!.toArray(false).toUByteArray()
    }
    if (0x133 in tlv) {
        sig.t133 = tlv[0x133]!!.toArray(false).toUByteArray()
    }
    if (0x106 in tlv) {
        sig.encryptedA1 = tlv[0x106]!!.toArray(false).toUByteArray()
    }
    sig.tgt = tlv[0x10A]!!.toArray(false).toUByteArray()
    sig.tgtKey = tlv[0x10D]!!.toArray(false).toUByteArray()
    sig.userStKey = tlv[0x10E]!!.toArray(false).toUByteArray()
    sig.userStWebSig = tlv[0x103]!!.toArray(false).toUByteArray()
    sig.sKey = tlv[0x120]!!.toArray(false).toUByteArray()
    sig.sKeyExpiredTime = System.currentTimeMillis() + 21600
    sig.d2 = tlv[0x143]!!.toArray(false).toUByteArray()
    sig.d2Key = TeaCipher.decodeByteKey(tlv[0x305]!!.toArray(false).toUByteArray())
    sig.deviceToken = tlv[0x322]!!.toArray(false).toUByteArray()

    @Suppress("DEPRECATION")
    val key = Hashing.md5().hashBytes(ByteBufAllocator.DEFAULT.buffer {
        writeBytes(passwordMD5)
        writeInt(0) // ByteArray(4)
        writeInt(uin.toInt())
    }.toArray(true)).asBytes().toUByteArray()
    QQTeaCipher(key).decrypt(ByteBufAllocator.DEFAULT.buffer(sig.encryptedA1!!.toByteArray())).use {
        if (it.readableBytes() > 51 + 16) {
            it.skipBytes(51)
            deviceInfo.tgtgtKey = ByteArray(16)
            it.readBytes(deviceInfo.tgtgtKey)
        }
    }
}
