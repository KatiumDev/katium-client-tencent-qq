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
package katium.client.qq.network.packet.login

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.tlv.*
import java.util.*

class PasswordLoginPacket(client: QQClient, val sequenceID: Int) :
    OicqPacket.Request.Simple(
        client = client,
        uin = client.uin.toInt(),
        command = 0x0810,
        encryption = OicqPacket.EncryptType.ECDH
    ) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID()) =
            TransportPacket.Request.Oicq(
                client = client,
                type = TransportPacket.Type.LOGIN,
                encryptType = TransportPacket.EncryptType.EMPTY_KEY,
                sequenceID = sequenceID,
                command = "wtlogin.login",
                packet = PasswordLoginPacket(client, sequenceID)
            )

    }

    override fun writeBody(output: ByteBuf) {
        output.apply {
            writeShort(9)
            writeShort(if (client.bot.options.allowSlider) 0x17 else 0x16)

            writeT18(uin = client.uin.toInt())
            writeT1(uin = client.uin.toInt(), ip = client.deviceInfo.ipAddress.map(Int::toByte).toByteArray())
            writeT106(
                uin = client.uin,
                subAppID = client.version.appID,
                ssoVersion = client.version.ssoVersion,
                passwordMD5 = client.passwordMD5,
                guidAvailable = true,
                guid = client.deviceInfo.guid,
                tgtgtKey = client.deviceInfo.tgtgtKey,
            )
            writeT116(miscBitmap = client.version.miscBitMap, subSigMap = client.version.subSigMap)
            writeT100(
                subAppID = client.version.subAppID,
                ssoVersion = client.version.ssoVersion,
                mainSigMap = client.version.mainSigMap
            )
            writeT107(0)
            writeT142(client.version.apkID.toByteArray())
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
                apkVersionName = client.version.version,
                apkSignatureMD5 = HexFormat.of().parseHex(client.version.signature)
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
            writeT194(imsiMD5 = HexFormat.of().parseHex(client.deviceInfo.IMSIMD5))
            if (client.bot.options.allowSlider) {
                writeT191()
            }
            @Suppress("DEPRECATION")
            writeT202(
                wifiBSSIDMD5 = Hashing.md5().hashBytes(client.deviceInfo.wifiBSSID.toByteArray()).asBytes(),
                wifiSSID = client.deviceInfo.wifiSSID.toByteArray()
            )
            writeT177(
                buildTime = client.version.buildTime,
                sdkVersion = client.version.sdkVersion.toByteArray()
            )
            writeT516()
            writeT521()
            writeT525(emptyArray())
        }
    }

}