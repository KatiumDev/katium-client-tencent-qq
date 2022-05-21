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
package katium.client.qq.network.packet.wtlogin

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.tlv.*
import java.util.*

class PasswordLoginPacket(client: QQClient, val sequenceID: Int) :
    OicqPacket.Request.Simple(client = client, uin = client.uin.toInt(), command = 0x0810, encryption = OicqPacket.EncryptType.ECDH) {

    override fun writeBody(output: ByteBuf) {
        output.apply {
            writeShort(9)
            writeShort(if (client.bot.allowSlider) 0x17 else 0x16)

            writeT18(uin = client.uin.toInt())
            writeT1(uin = client.uin.toInt(), ip = client.deviceInfo.ipAddress.map(Int::toByte).toByteArray())
            writeT106(
                uin = client.uin,
                subAppID = client.clientVersion.appID,
                ssoVersion = client.clientVersion.ssoVersion,
                passwordMD5 = client.passwordMD5,
                guidAvailable = true,
                guid = client.deviceInfo.guid,
                tgtgtKey = client.deviceInfo.tgtgtKey,
            )
            writeT116(miscBitmap = client.clientVersion.miscBitMap, subSigMap = client.clientVersion.subSigMap)
            writeT100(
                subAppID = client.clientVersion.subAppID,
                ssoVersion = client.clientVersion.ssoVersion,
                mainSigMap = client.clientVersion.mainSigMap
            )
            writeT107(0)
            writeT142(client.clientVersion.apkID.toByteArray())
            writeT144(
                imei = client.deviceInfo.IMEI.toByteArray(),
                deviceInfo = client.deviceInfo.toProtoBufDeviceInfo(),
                osType = client.deviceInfo.osType.toByteArray(),
                osVersion = client.deviceInfo.version.release.toByteArray(),
                simInfo = client.deviceInfo.simInfo.toByteArray(),
                apn = client.deviceInfo.apn.toByteArray(),
                buildModel = client.deviceInfo.model.toByteArray(),
                guid = client.deviceInfo.guid,
                buildBrand = client.deviceInfo.brand.toByteArray(),
                tgtgtKey = client.deviceInfo.tgtgtKey
            )
            writeT145(guid = client.deviceInfo.guid)
            writeT147(
                apkVersionName = client.clientVersion.version,
                apkSignatureMD5 = HexFormat.of().parseHex(client.clientVersion.signature)
            )
            /*if (client.clientVersion.miscBitMap and 0x80 != 0) {
                writeT166(1)
            }*/
            writeT154(sequenceID = sequenceID)
            writeT141(
                simInfo = client.deviceInfo.simInfo.toByteArray(),
                apn = client.deviceInfo.apn.toByteArray()
            )
            writeT8()
            writeT511()
            writeT187(macAddress = client.deviceInfo.macAddress.toByteArray())
            writeT188(androidID = client.deviceInfo.androidID.toByteArray())
            if (client.deviceInfo.IMSIMD5.isNotEmpty()) {
                writeT194(imsiMD5 = HexFormat.of().parseHex(client.deviceInfo.IMSIMD5))
            }
            if (client.bot.allowSlider) {
                writeT191()
            }
            if (client.deviceInfo.wifiBSSID.isNotEmpty() && client.deviceInfo.wifiSSID.isNotEmpty()) {
                @Suppress("DEPRECATION")
                writeT202(
                    wifiBSSIDMD5 = Hashing.md5().hashBytes(client.deviceInfo.wifiBSSID.toByteArray()).asBytes(),
                    wifiSSID = client.deviceInfo.wifiSSID.toByteArray()
                )
            }
            writeT177(
                buildTime = client.clientVersion.buildTime,
                sdkVersion = client.clientVersion.sdkVersion.toByteArray()
            )
            writeT516()
            writeT521()
            writeT525(emptyArray())
        }
    }

}