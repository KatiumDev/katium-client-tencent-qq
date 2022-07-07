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
import katium.client.qq.network.auth.DeviceInfo
import katium.client.qq.network.auth.NetworkType
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.core.util.netty.buffer
import katium.core.util.netty.use

context(TlvWriterContext) fun ByteBuf.writeT144(
    imei: ByteArray, // T109
    deviceInfo: DeviceInfo.ProtoBuf, // T52D
    // T124
    osType: ByteArray,
    osVersion: ByteArray,
    networkType: NetworkType = NetworkType.WIFI,
    simInfo: ByteArray,
    address: ByteArray = ByteArray(0),
    apn: ByteArray,
    // T128
    guidFromFileNull: Boolean = false,
    guidAvailable: Boolean = true,
    guidChanged: Boolean = false,
    guidFlag: Long = GUID_FLAG,
    buildModel: ByteArray, // T16E
    guid: ByteArray,
    buildBrand: ByteArray,
    // encrypt
    tgtgtKey: ByteArray
) = writeTlv(0x144) {
    QQTeaCipher(tgtgtKey.toUByteArray()).encrypt(alloc().buffer {
        writeTlvMap {
            writeT109(imei)
            writeT52D(deviceInfo)
            writeT124(osType, osVersion, networkType, simInfo, address, apn)
            writeT128(guidFromFileNull, guidAvailable, guidChanged, guidFlag, buildModel, guid, buildBrand)
            writeT16E(buildModel)
        }
    }).use {
        writeBytes(it)
    }
}