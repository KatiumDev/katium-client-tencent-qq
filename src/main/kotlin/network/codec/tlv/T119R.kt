package katium.client.qq.network.codec.tlv

import katium.client.qq.network.QQClient
import katium.core.util.netty.toArray

fun TlvMap.applyT119R(client: QQClient) {
    if (0x120 in this) {
        client.sig.sKey = this[0x120]!!.toArray(false).toUByteArray()
        client.sig.sKeyExpiredTime = System.currentTimeMillis() + 21600
    }
    if (0x11A in this) {
        val (age, gender, nick) = this[0x11A]!!.readT11A(false)
    }
}