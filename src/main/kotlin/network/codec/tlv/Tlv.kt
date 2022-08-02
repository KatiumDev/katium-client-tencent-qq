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

import io.netty.buffer.ByteBuf
import kotlinx.atomicfu.atomic

class TlvWriterContext {

    companion object {

        @JvmField
        val IGNORE = TlvWriterContext()

        inline fun <T> exempt(crossinline block: context(TlvWriterContext) () -> T) = with(IGNORE, block)

    }

    val tlvCount = atomic(0)

    fun recordTlvWrite() = tlvCount.incrementAndGet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TlvWriterContext) return false
        if (tlvCount != other.tlvCount) return false
        return true
    }

    override fun hashCode() = tlvCount.hashCode()

    override fun toString() = "TlvWriterContext(count=$tlvCount)"

}

context(TlvWriterContext) inline fun ByteBuf.writeTlv(type: Int, crossinline writer: ByteBuf.() -> Unit): ByteBuf {
    recordTlvWrite()
    writeShort(type)
    writeZero(2)
    val pos = writerIndex()
    val currentTlvCount = tlvCount.value
    writer()
    if (currentTlvCount != tlvCount.value) throw IllegalStateException("Nested TLV without context switching detected")
    setShort(pos - 2, writerIndex() - pos)
    return this
}

inline fun <T> ByteBuf.readTlv(release: Boolean, crossinline reader: ByteBuf.() -> T): T {
    skipBytes(2)
    val result = reader()
    if (release) {
        release()
    }
    return result
}

internal const val GUID_FLAG: Long = (1L shl 24 and 0xFF000000) or (0L shl 8 and 0xFF00)
