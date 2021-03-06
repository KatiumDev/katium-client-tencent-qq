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
package katium.client.qq.network.packet.meta

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket

class HeartbeatAlivePacket private constructor(client: QQClient, sequenceID: Int) : TransportPacket.Request.Simple(
    client = client,
    type = TransportPacket.Type.LOGIN,
    encryptType = TransportPacket.EncryptType.NONE,
    sequenceID = sequenceID,
    command = "Heartbeat.Alive"
) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID()) =
            HeartbeatAlivePacket(client, sequenceID)

    }

    override fun writeBody(output: ByteBuf) {
    }

}