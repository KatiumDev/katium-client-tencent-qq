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
import katium.client.qq.network.auth.NetworkType
import kotlin.math.min

context(TlvWriterContext) fun ByteBuf.writeT124(
    osType: ByteArray,
    osVersion: ByteArray,
    networkType: NetworkType = NetworkType.WIFI,
    simInfo: ByteArray,
    address: ByteArray = ByteArray(0),
    apn: ByteArray
) = writeTlv(0x124) {
    run {
        val length = min(osType.size, 16)
        writeShort(length)
        writeBytes(osType, 0, length)
    }
    run {
        val length = min(osVersion.size, 16)
        writeShort(length)
        writeBytes(osVersion, 0, length)
    }
    writeShort(networkType.value)
    run {
        val length = min(simInfo.size, 16)
        writeShort(length)
        writeBytes(simInfo, 0, length)
    }
    run {
        val length = min(address.size, 32)
        writeShort(length)
        writeBytes(address, 0, length)
    }
    run {
        val length = min(apn.size, 16)
        writeShort(length)
        writeBytes(apn, 0, length)
    }
}