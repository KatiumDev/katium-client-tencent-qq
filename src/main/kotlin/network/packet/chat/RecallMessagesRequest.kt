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

    fun createFriend(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        target: Long,
        sequence: Int,
        random: Int,
        time: Long,
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "PbMessageSvc.PbMsgWithDraw",
            body = ByteBufAllocator.DEFAULT.heapBuffer(
                createFriendRequest(client, target, sequence, random, time).toByteArray()
            )
        )

    fun createFriendRequest(
        client: QQClient,
        target: Long,
        sequence: Int,
        random: Int,
        time: Long,
    ): PbMessagePackets.RecallMessagesRequest = PbMessagePackets.RecallMessagesRequest.newBuilder()
        .addFriend(
            PbMessagePackets.RecallFriendMessagesRequest.newBuilder()
                .setSubCommand(1)
                .setLongMessageFlag(0)
                .addMessages(
                    PbMessagePackets.FriendMessageReference.newBuilder()
                        .setFromUin(client.uin)
                        .setToUin(target)
                        .setTime(time)
                        .setUid(0x0100000000000000 or (random.toLong() and 0xFFFFFFFF))
                        .setSequence(sequence)
                        .setRandom(random)
                        .setRoutingHead(
                            PbMessagePackets.RoutingHeader.newBuilder()
                                .setFriend(PbMessagePackets.ToFriend.newBuilder().setToUin(target))
                        )
                )
                .setReserved(ByteString.copyFrom(byteArrayOf(0x08, 0x00)))
        )
        .build()

    fun createGroup(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        groupCode: Long,
        sequence: Int,
        random: Int
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "PbMessageSvc.PbMsgWithDraw",
            body = ByteBufAllocator.DEFAULT.heapBuffer(
                createGroupRequest(
                    groupCode,
                    sequence,
                    random
                ).toByteArray()
            )
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