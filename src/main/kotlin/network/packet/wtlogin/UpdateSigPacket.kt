package katium.client.qq.network.packet.wtlogin

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.tlv.*

class UpdateSigPacket(client: QQClient, val sequenceID: Int, val mainSigMap: Int) :
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
                packet = UpdateSigPacket(client, sequenceID, mainSigMap)
            )

    }

    override fun writeBody(output: ByteBuf) {
        output.apply {
            writeShort(11)
            writeShort(17)

            writeT100(
                subAppID = 100,
                ssoVersion = client.clientVersion.ssoVersion,
                mainSigMap = mainSigMap
            )
            writeT10A(tgt = client.sig.tgt)
            writeT116(miscBitmap = client.clientVersion.miscBitMap, subSigMap = client.clientVersion.subSigMap)
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
        }
    }

}