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

/**
 * https://github.com/lz1998/rs-qq/blob/master/rq-engine/src/crypto/qqtea.rs
 * https://github.com/Mrs4s/MiraiGo/blob/master/binary/tea.go
 * https://github.com/mamoe/mirai/blob/master/mirai-core/src/commonMain/kotlin/utils/crypto/TEA.kt
 * https://github.com/takayama-lily/oicq/blob/main/lib/core/tea.ts
 */
@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalUnsignedTypes::class)
class TeaCipher(val key: UIntArray) {

    companion object {

        const val DELTA = 0x9E3779B9u // (sqrt(5) - 1) * 2 ^ 31

        fun decodeByteKey(bytes: UByteArray): UIntArray {
            if (bytes.size % 4 != 0) {
                throw IllegalArgumentException("The size of TEA bytes key should be multiplier of 4")
            }
            val result = UIntArray(bytes.size shr 2)
            for (index in result.indices) {
                val baseIndex = index * 4
                result[index] = (bytes[baseIndex].toUInt() shl 24) +
                        (bytes[baseIndex + 1].toUInt() shl 16) +
                        (bytes[baseIndex + 2].toUInt() shl 8) +
                        (bytes[baseIndex + 3].toUInt())
            }
            return result
        }

    }

    constructor(key: UByteArray) : this(decodeByteKey(key))

    init {
        if (key.size != 4) {
            throw IllegalArgumentException("The key of TEA crypto should be u32[4]")
        }
    }

    fun encrypt(data: ULong): ULong {
        var sum = 0u
        var x = (data shr 32).toUInt()
        var y = data.toUInt()

        for (i in 0u until 16u) {
            sum += DELTA
            x += (key[0] + (y shl 4)) xor (y + sum) xor (key[1] + (y shr 5))
            y += (key[2] + (x shl 4)) xor (x + sum) xor (key[3] + (x shr 5))
        }

        return (x.toULong() shl 32) or y.toULong()
    }

    fun decrypt(data: ULong): ULong {
        var sum = DELTA shl 4
        var x = (data shr 32).toUInt()
        var y = data.toUInt()

        for (i in 0u until 16u) {
            y -= (key[2] + (x shl 4)) xor (x + sum) xor (key[3] + (x shr 5))
            x -= (key[0] + (y shl 4)) xor (y + sum) xor (key[1] + (y shr 5))
            sum -= DELTA
        }

        return (x.toULong() shl 32) or y.toULong()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TeaCipher) return false
        if (key != other.key) return false
        return true
    }

    override fun hashCode() = key.hashCode()

    override fun toString() = "TeaCipher(key=$key)"

}