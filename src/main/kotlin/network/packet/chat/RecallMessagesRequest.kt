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
import katium.client.qq.network.pb.RoutingHeader
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RecallMessagesRequest(
    @ProtoNumber(1) val friends: Set<FriendRequest> = emptySet(),
    @ProtoNumber(2) val groups: Set<GroupRequest> = emptySet(),
) {

    companion object {

        fun createFriend(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            target: Long,
            sequence: Int,
            random: Int,
            time: Long,
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "PbMessageSvc.PbMsgWithDraw",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                ProtoBuf.encodeToByteArray(
                    createFriendRequest(
                        client, target, sequence, random, time
                    )
                )
            )
        )

        fun createFriendRequest(
            client: QQClient,
            target: Long,
            sequence: Int,
            random: Int,
            time: Long,
        ) = RecallMessagesRequest(
            friends = setOf(
                FriendRequest(
                    subCommand = 1, longMessageFlag = 0, messages = setOf(
                        FriendMessageReference(
                            fromUin = client.uin,
                            toUin = target,
                            time = time,
                            uid = 0x0100000000000000 or (random.toLong() and 0xFFFFFFFF),
                            sequence = sequence,
                            random = random,
                            routingHeader = RoutingHeader(friend = RoutingHeader.ToFriend(toUin = target))
                        )
                    ), reserved = byteArrayOf(0x08, 0x00)
                )
            )
        )

        fun createGroup(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            groupCode: Long,
            sequence: Int,
            random: Int
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "PbMessageSvc.PbMsgWithDraw",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                ProtoBuf.encodeToByteArray(
                    createGroupRequest(
                        groupCode, sequence, random
                    )
                )
            )
        )

        fun createGroupRequest(
            groupCode: Long, sequence: Int, random: Int
        ) = RecallMessagesRequest(
            groups = setOf(
                GroupRequest(
                    subCommand = 1, groupCode = groupCode, messages = setOf(
                        GroupMessageReference(
                            sequence = sequence,
                            random = random,
                            type = 0
                        )
                    ), userDef = byteArrayOf(0x08, 0x00)
                )
            )
        )

    }

    @Serializable
    data class FriendRequest(
        @ProtoNumber(1) val messages: Set<FriendMessageReference> = emptySet(),
        @ProtoNumber(2) val longMessageFlag: Int? = null,
        @ProtoNumber(3) val reserved: ByteArray? = null,
        @ProtoNumber(4) val subCommand: Int? = null,
    )

    @Serializable
    data class GroupRequest(
        @ProtoNumber(1) val subCommand: Int,
        @ProtoNumber(2) val groupType: Int? = null,
        @ProtoNumber(3) val groupCode: Long,
        @ProtoNumber(4) val messages: Set<GroupMessageReference> = emptySet(),
        @ProtoNumber(5) val userDef: ByteArray,
    )

    @Serializable
    data class GroupMessageReference(
        @ProtoNumber(1) val sequence: Int,
        @ProtoNumber(2) val random: Int,
        @ProtoNumber(3) val type: Int,
    )

    @Serializable
    data class FriendMessageReference(
        @ProtoNumber(1) val fromUin: Long,
        @ProtoNumber(2) val toUin: Long,
        @ProtoNumber(3) val sequence: Int,
        @ProtoNumber(4) val uid: Long,
        @ProtoNumber(5) val time: Long,
        @ProtoNumber(6) val random: Int,
        @ProtoNumber(7) val packageNumber: Int? = null,
        @ProtoNumber(8) val packageIndex: Int? = null,
        @ProtoNumber(9) val divideSequence: Int? = null,
        @ProtoNumber(10) val type: Int? = null,
        @ProtoNumber(20) val routingHeader: RoutingHeader,
    )

}