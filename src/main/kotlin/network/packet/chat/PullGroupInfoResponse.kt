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
package katium.client.qq.network.packet.chat

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oidb.readOidbPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbGroupInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
class PullGroupInfoResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: Data
        private set

    override fun readBody(input: ByteBuf) {
        response = ProtoBuf.decodeFromByteArray(input.readOidbPacket().buffer)
    }

    @Serializable
    data class Data(
        @ProtoNumber(1) val info: Set<Info> = emptySet(),
        @ProtoNumber(2) val errorInfo: ByteArray? = null,
    )

    @Serializable
    data class Info(
        @ProtoNumber(1) val groupCode: Long,
        @ProtoNumber(2) val result: Int,
        @ProtoNumber(3) val info: PbGroupInfo,
    )

}
