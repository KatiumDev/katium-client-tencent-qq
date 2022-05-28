package katium.client.qq.network.packet.wtlogin

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.tlv.applyT119
import katium.client.qq.network.codec.tlv.applyT119R
import katium.client.qq.network.codec.tlv.readT119
import katium.client.qq.network.codec.tlv.readTlvMap

class UpdateSigResponse(client: QQClient, uin: Int, command: Short) :
    OicqPacket.Response.Simple(client, uin, command) {

    var type: Int = 0
        private set
    var result: Int = 0
        private set

    override fun readBody(input: ByteBuf) {
        type = input.readShort().toInt()
        result = input.readByte().toInt()
        if (result != 0) throw IllegalStateException("Unable to exchange_emp, type=$type, result=$result")
        input.skipBytes(2)
        val tlv = input.readTlvMap(2, release = false)
        when (type) {
            15 -> {
                tlv[0x119]!!.readT119(client.deviceInfo.tgtgtKey, release = false).use {
                    it.applyT119R(client)
                }
            }
            11 -> {
                @Suppress("DEPRECATION")
                tlv[0x119]!!.readT119(
                    Hashing.md5().hashBytes(client.sig.d2KeyEncoded.toByteArray()).asBytes(),
                    release = false
                ).use {
                    it.applyT119(client)
                }
            }
            else -> throw UnsupportedOperationException("Unknown exchange_emp response type: $type")
        }
    }

}