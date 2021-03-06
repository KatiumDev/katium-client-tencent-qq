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
import katium.client.qq.network.pb.PbMultiMessages
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

object MultiMessagesDownloadRequest {

    @OptIn(ExperimentalSerializationApi::class)
    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        buType: Int,
        resourceID: String,
    ) = TransportPacket.Request.Buffered(
        client = client,
        type = TransportPacket.Type.SIMPLE,
        encryptType = TransportPacket.EncryptType.D2_KEY,
        sequenceID = sequenceID,
        command = "MultiMsg.ApplyDown",
        body = PooledByteBufAllocator.DEFAULT.heapBuffer(
            ProtoBuf.encodeToByteArray(
                createRequest(
                    client,
                    buType,
                    resourceID
                )
            )
        )
    )

    fun createRequest(
        client: QQClient,
        buType: Int,
        resourceID: String,
    ) = PbMultiMessages.Request(
        subCommand = 2,
        termType = 5,
        platformType = 9,
        networkType = 3,
        buildVersion = client.version.version,
        buType = buType,
        channelType = 2,
        downloads = listOf(
            PbMultiMessages.Download.Request(
                resourceID = resourceID.toByteArray(),
                type = 3,
                fromUin = client.uin,
                key = client.highway.sessionKey ?: ByteArray(0),
                sig = client.highway.sessionSig ?: ByteArray(0)
            )
        )
    )

}