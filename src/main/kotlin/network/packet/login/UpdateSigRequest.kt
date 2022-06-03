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

class UpdateSigRequest(client: QQClient, val sequenceID: Int, val mainSigMap: Int) :
    OicqPacket.Request.Simple(
        client = client,
        uin = client.uin.toInt(),
        command = 0x0810,
        encryption = OicqPacket.EncryptType.ECDH
    ) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID(), mainSigMap: Int) =
            TransportPacket.Request.Oicq(
                client = client,
                type = TransportPacket.Type.LOGIN,
                encryptType = TransportPacket.EncryptType.EMPTY_KEY,
                sequenceID = sequenceID,
                command = "wtlogin.exchange_emp",
                packet = UpdateSigRequest(client, sequenceID, mainSigMap)
            )

    }

    override fun writeBody(output: ByteBuf) {
        output.apply {
            writeShort(11)
            writeShort(17) // TLV count

            writeT100(
                subAppID = 100,
                ssoVersion = client.version.ssoVersion,
                mainSigMap = mainSigMap
            )
            writeT10A(tgt = client.sig.tgt)
            writeT116(miscBitmap = client.version.miscBitMap, subSigMap = client.version.subSigMap)
            writeT108(ksid = client.sig.ksid)
            @Suppress("DEPRECATION")
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
                tgtgtKey = Hashing.md5().hashBytes(client.sig.d2KeyEncoded.toByteArray()).asBytes()
            )
            writeT143(d2 = client.sig.d2)
            writeT142(apkID = client.version.apkID.toByteArray())
            writeT154(sequenceID = sequenceID)
            writeT141(
                simInfo = client.deviceInfo.simInfo.toByteArray(),
                apn = client.deviceInfo.apn.toByteArray()
            )
            writeT8()
            writeT147(
                apkVersionName = client.version.version,
                apkSignatureMD5 = HexFormat.of().parseHex(client.version.signature)
            )
            writeT177(
                buildTime = client.version.buildTime,
                sdkVersion = client.version.sdkVersion.toByteArray()
            )
            writeT187(macAddress = client.deviceInfo.macAddress.toByteArray())
            writeT188(androidID = client.deviceInfo.androidID.toByteArray())
            writeT194(imsiMD5 = HexFormat.of().parseHex(client.deviceInfo.IMSIMD5))
            writeT511()
            @Suppress("DEPRECATION")
            writeT202(
                wifiBSSIDMD5 = Hashing.md5().hashBytes(client.deviceInfo.wifiBSSID.toByteArray()).asBytes(),
                wifiSSID = client.deviceInfo.wifiSSID.toByteArray()
            )
        }
    }

}