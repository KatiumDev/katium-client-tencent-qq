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
package katium.client.qq.network.crypto.tea

import io.netty.buffer.ByteBuf
import katium.core.util.netty.getULong
import katium.core.util.netty.setULong
import java.util.*
import kotlin.experimental.and
import kotlin.random.Random

/**
 * https://github.com/lz1998/rs-qq/blob/master/rq-engine/src/crypto/qqtea.rs
 * https://github.com/Mrs4s/MiraiGo/blob/master/binary/tea.go
 * https://github.com/mamoe/mirai/blob/master/mirai-core/src/commonMain/kotlin/utils/crypto/TEA.kt
 * https://github.com/takayama-lily/oicq/blob/main/lib/core/tea.ts
 */
class QQTeaCipher(val cipher: TeaCipher, val random: Random = Random.Default) {

    companion object {

        @JvmField
        val TAIL_BYTES = ByteArray(7)

    }

    constructor(key: UByteArray) : this(TeaCipher(key))

    constructor(vararg key: UInt) : this(TeaCipher(key))

    fun encrypt(data: ByteBuf, release: Boolean = true): ByteBuf {
        val fillSize = 9 - ((data.readableBytes() + 1) % 8)
        val buffer = data.alloc().buffer(1 + fillSize + data.readableBytes() + 7)
        buffer.writeByte((fillSize - 2) or (random.nextInt() and 0b11111000))
        for (i in 0 until fillSize) {
            buffer.writeByte(random.nextInt())
        }
        buffer.writeBytes(data)
            .writeBytes(TAIL_BYTES)

        var iv1 = 0uL
        var iv2 = 0uL
        var holder: ULong
        for (index in 0 until buffer.writerIndex() step 8) {
            val block = buffer.getULong(index)
            holder = block xor iv1
            iv1 = cipher.encrypt(holder) xor iv2
            iv2 = holder
            buffer.setULong(index, iv1)
        }

        if (release) {
            data.release()
        }

        assert(buffer.writerIndex() == buffer.capacity())
        assert(buffer.readableBytes() % 8 == 0)
        return buffer
    }

    fun decrypt(buffer: ByteBuf, release: Boolean = true): ByteBuf {
        if (buffer.readableBytes() < 16 || buffer.readableBytes() % 8 != 0) {
            throw IllegalArgumentException("Size of QQTea encrypted data should greater than 16 and multiplier of 8 but got ${buffer.readableBytes()}")
        }

        var iv1: ULong
        var iv2 = 0uL
        var holder = 0uL
        for (index in buffer.readerIndex() until buffer.writerIndex() step 8) {
            iv1 = buffer.getULong(index)
            iv2 = iv2 xor iv1
            iv2 = cipher.decrypt(iv2)
            buffer.setULong(index, iv2 xor holder)
            holder = iv1
        }

        val fillSize = (buffer.readByte() and 0b00000111) + 2
        val dataLength = buffer.readableBytes() - 7 - fillSize

        buffer.skipBytes(fillSize)
        val result = buffer.readBytes(dataLength)

        // Check fill bytes
        val tailBytes = ByteArray(7)
        buffer.readBytes(tailBytes)
        if (!tailBytes.contentEquals(TAIL_BYTES)) {
            throw IllegalArgumentException(
                "QQTea encrypted data tail bytes are not correct, got ${
                    HexFormat.of().formatHex(tailBytes)
                }"
            )
        }

        if (release) {
            buffer.release()
        }
        return result
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