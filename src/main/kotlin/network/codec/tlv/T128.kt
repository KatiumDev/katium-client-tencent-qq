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

fun ByteBuf.writeT128(
    guidFromFileNull: Boolean = false,
    guidAvailable: Boolean = true,
    guidChanged: Boolean = false,
    guidFlag: Long = GUID_FLAG,
    buildModel: ByteArray,
    guid: ByteArray,
    buildBrand: ByteArray
) = writeTlv(0x128) {
    writeShort(0)
    writeBoolean(guidFromFileNull)
    writeBoolean(guidAvailable)
    writeBoolean(guidChanged)
    writeInt(guidFlag.toInt())
    run {
        val length = min(buildModel.size, 32)
        writeShort(length)
        writeBytes(buildModel, 0, length)
    }
    run {
        val length = min(guid.size, 16)
        writeShort(length)
        writeBytes(guid, 0, length)
    }
    run {
        val length = min(buildBrand.size, 16)
        writeShort(length)
        writeBytes(buildBrand, 0, length)
    }
}