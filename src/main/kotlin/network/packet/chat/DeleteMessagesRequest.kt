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
import katium.client.qq.network.pb.PbDeleteMessages
import katium.core.util.netty.heapBuffer

object DeleteMessagesRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        items: Collection<PbDeleteMessages.MessageItem>
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "MessageSvc.PbDeleteMsg",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(createRequest(items).toByteArray())
        )

    fun createRequest(items: Collection<PbDeleteMessages.MessageItem>): PbDeleteMessages.DeleteMessagesRequest =
        PbDeleteMessages.DeleteMessagesRequest.newBuilder().addAllItems(items).build()

}