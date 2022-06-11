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
import kotlin.math.min

context(TlvWriterContext) fun ByteBuf.writeT202(wifiBSSIDMD5: ByteArray, wifiSSID: ByteArray) = writeTlv(0x202) {
    run {
        val length = min(wifiBSSIDMD5.size, 16)
        writeShort(length)
        writeBytes(wifiBSSIDMD5, 0, length)
    }
    run {
        val length = min(wifiSSID.size, 32)
        writeShort(length)
        writeBytes(wifiSSID, 0, length)
    }
}