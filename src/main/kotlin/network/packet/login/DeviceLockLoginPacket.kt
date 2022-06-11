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

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.tlv.*

class DeviceLockLoginPacket(client: QQClient, val sequenceID: Int) :
    OicqPacket.Request.Simple(
        client = client,
        uin = client.uin.toInt(),
        command = 0x0810,
        encryption = OicqPacket.EncryptType.ECDH,
        subCommand = 20,
    ) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID()) =
            TransportPacket.Request.Oicq(
                client = client,
                type = TransportPacket.Type.LOGIN,
                encryptType = TransportPacket.EncryptType.EMPTY_KEY,
                sequenceID = sequenceID,
                command = "wtlogin.login",
                packet = DeviceLockLoginPacket(client, sequenceID)
            )

    }

    override fun writeBody(output: ByteBuf) {
        output.writeTlvMap {
            writeT8()
            writeT104(t104 = client.sig.t104)
            writeT116(miscBitmap = client.version.miscBitMap, subSigMap = client.version.subSigMap)
            writeT401(g = client.sig.g!!)
        }
    }

}