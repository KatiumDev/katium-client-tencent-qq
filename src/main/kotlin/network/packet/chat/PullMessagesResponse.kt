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
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.message.pb.PbMessage
import katium.client.qq.network.sync.SyncFlag
import katium.core.util.netty.toArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
class PullMessagesResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: Data
        private set

    override fun readBody(input: ByteBuf) {
        response = ProtoBuf.decodeFromByteArray(input.toArray(release = false))
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class Data(
        @ProtoNumber(1) val result: Int,
        @ProtoNumber(2) val errorMessage: String? = null,
        @ProtoNumber(3) val syncCookie: ByteArray? = null,
        @ProtoNumber(4) val syncFlag: SyncFlag,
        @ProtoNumber(5) val messages: List<UinPairMessage> = emptyList(),
        @ProtoNumber(6) val uin: Long? = null,
        @ProtoNumber(7) val syncType: Int,
        @ProtoNumber(8) val publicAccountCookie: ByteArray? = null,
        @ProtoNumber(9) val isPartialSync: Boolean? = null,
        @ProtoNumber(10) val controlBuffer: ByteArray? = null,
    )

    @Serializable
    data class UinPairMessage(
        @ProtoNumber(1) val lastReadTime: Int,
        @ProtoNumber(2) val peerUin: Long,
        @ProtoNumber(3) val completed: Int? = null,
        @ProtoNumber(4) val messages: List<PbMessage> = emptyList(),
    )

}