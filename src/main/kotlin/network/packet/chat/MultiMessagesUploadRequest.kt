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

import com.google.common.hash.HashCode
import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMultiMessages
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

object MultiMessagesUploadRequest {

    @OptIn(ExperimentalSerializationApi::class)
    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        buType: Int,
        groupUin: Long,
        size: Int,
        md5: HashCode,
    ) = TransportPacket.Request.Buffered(
        client = client,
        type = TransportPacket.Type.SIMPLE,
        encryptType = TransportPacket.EncryptType.D2_KEY,
        sequenceID = sequenceID,
        command = "MultiMsg.ApplyUp",
        body = PooledByteBufAllocator.DEFAULT.heapBuffer(
            ProtoBuf.encodeToByteArray(
                createRequest(
                    client, buType, groupUin, size, md5
                )
            )
        )
    )

    fun createRequest(
        client: QQClient,
        buType: Int,
        groupUin: Long,
        size: Int,
        md5: HashCode,
    ) = PbMultiMessages.Request(
        subCommand = 1,
        termType = 5,
        platformType = 9,
        networkType = 3,
        buildVersion = client.version.version,
        channelType = 0,
        buType = buType,
        uploads = listOf(
            PbMultiMessages.Upload.Request(
                toUin = groupUin, size = size.toLong(), md5 = md5.asBytes(), type = 3, applyId = 0
            )
        )
    )

}