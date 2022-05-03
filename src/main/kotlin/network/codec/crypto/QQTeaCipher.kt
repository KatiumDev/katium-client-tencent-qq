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
package katium.client.qq.network.codec.crypto

import io.netty.buffer.ByteBuf
import katium.core.util.netty.getULong
import katium.core.util.netty.setULong
import kotlin.experimental.and
import kotlin.random.Random

/**
 * https://github.com/lz1998/rs-qq/blob/master/rq-engine/src/crypto/qqtea.rs
 * https://github.com/Mrs4s/MiraiGo/blob/master/binary/tea.go
 * https://github.com/mamoe/mirai/blob/master/mirai-core/src/commonMain/kotlin/utils/crypto/TEA.kt
 * https://github.com/takayama-lily/oicq/blob/main/lib/core/tea.ts
 */
class QQTeaCipher(val cipher: TeaCipher, val random: Random = Random.Default) {

    fun encrypt(data: ByteBuf): ByteBuf {
        val fillSize = 9 - ((data.readableBytes() + 1) % 8)
        val buffer = data.alloc().buffer(1 + fillSize + data.readableBytes() + 7)
        buffer.writeByte((fillSize - 2) or (random.nextInt() and 0b11111000))
        for (i in 0 until fillSize) {
            buffer.writeByte(random.nextInt())
        }
        buffer.writeBytes(data)
            .writeBytes(byteArrayOf(0, 0, 0, 0, 0, 0, 0)) // fill padding
        var iv1 = 0uL
        var iv2 = 0uL
        var holder: ULong

        for (index in 0 until buffer.readableBytes() step 8) {
            val block = buffer.getULong(index)
            holder = block xor iv1
            iv1 = cipher.encrypt(holder)
            iv1 = iv1 xor iv2
            iv2 = holder
            buffer.setULong(index, iv1)
        }

        return buffer
    }

    fun decrypt(buffer: ByteBuf): ByteBuf {
        if (buffer.readableBytes() < 16 || buffer.readableBytes() % 8 != 0) {
            throw IllegalArgumentException("Size of QQTea encrypted data should greater than 16 and multiplier of 8")
        }
        var iv1: ULong
        var iv2 = 0uL
        var holder = 0uL

        for (index in buffer.readerIndex() until buffer.writerIndex() step 8) {
            val block = buffer.getULong(index)
            iv1 = block
            iv2 = iv2 xor iv1
            iv2 = cipher.decrypt(iv2)
            buffer.setULong(index, iv2 xor holder)
            holder = iv1
        }

        val fillSize = (buffer.readByte() and 0b00000111) + 2
        val dataLength = buffer.readableBytes() - 7 - fillSize
        return buffer.copy(buffer.readerIndex() + fillSize, dataLength)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQTeaCipher) return false
        if (cipher != other.cipher) return false
        if (random != other.random) return false
        return true
    }

    override fun hashCode(): Int {
        var result = cipher.hashCode()
        result = 31 * result + random.hashCode()
        return result
    }

    override fun toString() = "QQTeaCipher(cipher=$cipher, random=$random)"

}