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
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMessagePackets
import katium.core.util.netty.heapBuffer

object RecallMessagesRequest {

    fun createGroup(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        groupCode: Long,
        sequence: Int,
        messageType: Int
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "PbMessageSvc.PbMsgWithDraw",
            body = ByteBufAllocator.DEFAULT.heapBuffer(createGroupRequest(groupCode, sequence, messageType).toByteArray())
        )

    fun createGroupRequest(
        groupCode: Long,
        sequence: Int,
        random: Int
    ): PbMessagePackets.RecallMessagesRequest = PbMessagePackets.RecallMessagesRequest.newBuilder()
        .addGroup(
            PbMessagePackets.RecallGroupMessagesRequest.newBuilder()
                .setSubCommand(1)
                .setGroupCode(groupCode)
                .addMessages(
                    PbMessagePackets.GroupMessageReference.newBuilder()
                        .setSequence(sequence)
                        .setRandom(random)
                        .setType(0)
                )
                .setUserDef(ByteString.copyFrom(byteArrayOf(0x08, 0x00)))
        )
        .build()

}