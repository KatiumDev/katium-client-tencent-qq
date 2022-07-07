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

import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class DeleteMessagesRequest(
    @ProtoNumber(1) val items: List<Item> = emptyList(),
) {

    companion object {

        fun create(
            client: QQClient, sequenceID: Int = client.allocPacketSequenceID(), items: List<Item>
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "MessageSvc.PbDeleteMsg",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(ProtoBuf.encodeToByteArray(DeleteMessagesRequest(items)))
        )

    }

    @Serializable
    data class Item(
        @ProtoNumber(1) val fromUin: Long,
        @ProtoNumber(2) val toUin: Long,
        @ProtoNumber(3) val type: Int,
        @ProtoNumber(4) val sequence: Int,
        @ProtoNumber(5) val uid: Long,
        @ProtoNumber(7) val sig: ByteArray,
    )

}