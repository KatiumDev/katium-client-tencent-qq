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
data class PullGroupHistoryMessagesRequest(
    @ProtoNumber(1) val groupCode: Long,
    @ProtoNumber(2) val beginSequence: Long,
    @ProtoNumber(3) val endSequence: Long,
    @ProtoNumber(4) val filter: Int? = null,
    @ProtoNumber(5) val memberSequence: Long? = null,
    @ProtoNumber(6) val publicGroup: Boolean,
    @ProtoNumber(7) val shieldFlag: Int? = null,
    @ProtoNumber(8) val saveTrafficFlag: Int? = null,
) {

    companion object {

        fun create(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            groupCode: Long,
            beginSequence: Long,
            endSequence: Long
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "MessageSvc.PbGetGroupMsg",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                ProtoBuf.encodeToByteArray(
                    createRequest(
                        groupCode, beginSequence, endSequence
                    )
                )
            )
        )

        fun createRequest(
            groupCode: Long, beginSequence: Long, endSequence: Long
        ) = PullGroupHistoryMessagesRequest(
            groupCode = groupCode, beginSequence = beginSequence, endSequence = endSequence, publicGroup = false
        )

    }

}