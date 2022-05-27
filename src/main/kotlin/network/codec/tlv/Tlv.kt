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
import katium.core.util.netty.readUShort

/**
 * https://cs.github.com/Mrs4s/MiraiGo/tree/master/internal/tlv
 * https://cs.github.com/lz1998/rs-qq/blob/master/rq-engine/src/command/wtlogin/tlv_writer.rs
 * https://github.com/takayama-lily/oicq/blob/main/lib/core/tlv.ts
 * https://github.com/mamoe/mirai/blob/dev/mirai-core/src/commonMain/kotlin/network/protocol/packet/Tlv.kt
 */
inline fun ByteBuf.writeTlv(command: Int, crossinline writer: ByteBuf.() -> Unit): ByteBuf {
    writeShort(command)
    writeZero(2)
    val pos = writerIndex()
    writer()
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

/**
 * https://github.com/mamoe/mirai/blob/dev/mirai-core/src/commonMain/kotlin/network/protocol/packet/Tlv.kt#L464
 */
internal const val GUID_FLAG: Long = (1L shl 24 and 0xFF000000) or (0L shl 8 and 0xFF00)
