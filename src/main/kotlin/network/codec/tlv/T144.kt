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
package katium.client.qq.network.codec.tlv

import io.netty.buffer.ByteBuf
import katium.client.qq.network.auth.NetworkType
import katium.client.qq.network.crypto.tea.QQTeaCipher
import katium.client.qq.network.pb.PbDeviceInfo
import katium.core.util.netty.buffer
import katium.core.util.netty.use

fun ByteBuf.writeT144(
    imei: ByteArray, // T109
    deviceInfo: PbDeviceInfo.DeviceInfo, // T52D
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
        writeShort(5) // tlv count
        writeT109(imei)
        writeT52D(deviceInfo)
        writeT124(osType, osVersion, networkType, simInfo, address, apn)
        writeT128(guidFromFileNull, guidAvailable, guidChanged, guidFlag, buildModel, guid, buildBrand)
        writeT16E(buildModel)
    }).use {
        writeBytes(it)
    }
}