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
        input.skipBytes(2)
        val tlv = input.readTlvMap(2, release = false)
        if (result != 0) throw IllegalStateException("Unable to exchange_emp, type=$type, result=$result, tlv=$tlv")
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