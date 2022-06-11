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

import com.google.protobuf.ByteString
import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMultiMessages
import katium.core.util.netty.heapBuffer

object MultiMessagesDownloadRequest {

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
            createRequest(client, buType, resourceID).toByteArray()
        )
    )

    fun createRequest(
        client: QQClient,
        buType: Int,
        resourceID: String,
    ): PbMultiMessages.MultiMessagesRequest =
        PbMultiMessages.MultiMessagesRequest.newBuilder().setSubCommand(2).setTermType(5).setPlatformType(9)
            .setNetworkType(3).setBuildVersion(client.version.version).setBuType(buType).setChannelType(2)
            .addDownload(PbMultiMessages.MultiMessagesDownloadRequest.newBuilder()
                .setResourceID(ByteString.copyFrom(resourceID.toByteArray())).setType(3).setFromUin(client.uin).apply {
                    if (client.highway.sessionKey != null) key = ByteString.copyFrom(client.highway.sessionKey)
                    if (client.highway.sessionSig != null) sig = ByteString.copyFrom(client.highway.sessionSig)
                }).build()

}