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
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.client.qq.network.codec.crypto.tea.TeaCipher
import katium.core.util.netty.buffer
import katium.core.util.netty.toArray
import katium.core.util.netty.use

fun ByteBuf.readT119(key: ByteArray, release: Boolean = true): TlvMap =
    QQTeaCipher(key.toUByteArray()).decrypt(this, release = release).skipBytes(2).readTlvMap()

fun TlvMap.applyT119(client: QQClient) {
    /*if(0x130 in this) {
        applyT130(t130)
    }*/
    /*if(0x113 in this) {
        applyT130(0x113)
    }*/
    /*if(0x528 in this) {
        client.sig.t528 = this[0x528]!!.toArray(false)
    }*/
    /*if(0x530 in this) {
        client.sig.t530 = this[0x530]!!.toArray(false)
    }*/
    if (0x108 in this) {
        client.sig.ksid = this[0x108]!!.toArray(false)
    }
    val (age, gender, nick) = this[0x11A]!!.readT11A(false)
    // @TODO: handle self summary card info
    /*if (0x125 in this {
        val (openID, openKey) = this[0x125]!!.readT125(false)
    }*/
    /*if(0x186 in this) {
        applyT186(0x186)
    }*/
    /*if (0x199 in this) {
        val (openID, payToken) = this[0x199]!!.readT199(false)
    }*/
    /*if (0x200 in this) {
        val (pf, pfKey) = this[0x200]!!.readT200(false)
    }*/
    /*if (0x531 in this) {
        val (a1, noPicSig) = this[0x531]!!.readT531(false)
    }*/
    /*if (0x138 in this) {
        this[0x200]!!.readT138(false)
    }*/
    if (0x512 in this) {
        val (psKeyMap, pt4TokenMap) = this[0x512]!!.readT512(false)
        client.sig.psKeyMap = psKeyMap
        client.sig.pt4TokenMap = pt4TokenMap
    }
    if (0x134 in this) {
        client.oicqCodec.wtSessionTicketKey = this[0x134]!!.toArray(false)
        client.oicqCodec.wtSessionTicketKeyCipher = QQTeaCipher(client.oicqCodec.wtSessionTicketKey!!.toUByteArray())
    }
    client.sig.loginBitmap = 0uL
    if (0x16A in this) {
        client.sig.srmToken = this[0x16A]!!.toArray(false)
    }
    if (0x133 in this) {
        client.sig.t133 = this[0x133]!!.toArray(false)
    }
    if (0x106 in this) {
        client.sig.encryptedA1 = this[0x106]!!.toArray(false)
    }
    client.sig.tgt = this[0x10A]!!.toArray(false)
    client.sig.tgtKey = this[0x10D]!!.toArray(false)
    client.sig.userStKey = this[0x10E]!!.toArray(false)
    client.sig.userStWebSig = this[0x103]!!.toArray(false)
    client.sig.sKey = this[0x120]!!.toArray(false)
    client.sig.sKeyExpiredTime = System.currentTimeMillis() + 21600
    client.sig.d2 = this[0x143]!!.toArray(false)
    client.sig.d2KeyEncoded = this[0x305]!!.toArray(false).toUByteArray()
    synchronized(client.sig) { client.sig.d2Key = TeaCipher.decodeByteKey(client.sig.d2KeyEncoded) }
    if (0x322 in this) client.sig.deviceToken = this[0x322]!!.toArray(false)

    @Suppress("DEPRECATION")
    val key = Hashing.md5().hashBytes(ByteBufAllocator.DEFAULT.buffer {
        writeBytes(client.passwordMD5)
        writeInt(0) // ByteArray(4)
        writeInt(client.uin.toInt())
    }.toArray(true)).asBytes().toUByteArray()
    QQTeaCipher(key).decrypt(ByteBufAllocator.DEFAULT.buffer(client.sig.encryptedA1!!)).use {
        if (it.readableBytes() > 51 + 16) {
            it.skipBytes(51)
            client.deviceInfo.tgtgtKey = ByteArray(16)
            it.readBytes(client.deviceInfo.tgtgtKey)
        }
    }
}
