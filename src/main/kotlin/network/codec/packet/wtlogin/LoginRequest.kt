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
package katium.client.qq.network.codec.packet.wtlogin

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.crypto.EncryptType
import katium.client.qq.network.codec.struct.oicq.OicqPacket
import katium.client.qq.network.codec.struct.packet.RequestPacket
import katium.client.qq.network.codec.struct.packet.PacketType
import katium.client.qq.network.codec.struct.tlv.*
import katium.core.util.netty.buffer
import katium.core.util.netty.writeUByteArray
import java.util.*

fun createLoginRequest(client: QQClient, sequenceID: Int) = RequestPacket(
    type = PacketType.LOGIN,
    encryptType = EncryptType.EMPTY_KEY,
    uin = client.uin,
    sequenceID = sequenceID,
    command = "wtlogin.login",
    body = ByteBufAllocator.DEFAULT.buffer {
        client.oicqCodec.encode(this, OicqPacket(
            uin = client.uin.toInt(),
            command = 0x0810,
            body = ByteBufAllocator.DEFAULT.buffer {
                writeShort(9)
                writeShort(24)

                writeT18(uin = client.uin.toInt())
                writeT1(uin = client.uin.toInt(), ip = client.deviceInfo.ipAddress.map(Int::toByte).toByteArray())
                writeT106(
                    uin = client.uin.toInt(),
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
                if ((client.bot.config["qq.allow_slider"] ?: "false").toBoolean()) {
                    writeT191()
                }
                if (client.deviceInfo.wifiBSSID.isNotEmpty() && client.deviceInfo.wifiSSID.isNotEmpty()) {
                    writeT202(
                        wifiBSSID = client.deviceInfo.wifiBSSID.toByteArray(),
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
        ))
    }
)

data class LoginExtraData(
    val uin: Long,
    val ip: UByteArray,
    val time: Int,
    val version: Int
)

fun ByteBuf.writeLoginExtraData(data: LoginExtraData): ByteBuf {
    writeLong(data.uin)
    writeByte(data.ip.size)
    writeUByteArray(data.ip)
    writeInt(data.time)
    writeInt(data.version)
    return this
}
