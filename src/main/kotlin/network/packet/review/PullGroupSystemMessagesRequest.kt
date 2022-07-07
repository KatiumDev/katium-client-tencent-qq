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
package katium.client.qq.network.packet.review

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
data class PullGroupSystemMessagesRequest(
    @ProtoNumber(1) val messageNumber: Int,
    @ProtoNumber(2) val latestFriendSequence: Long,
    @ProtoNumber(3) val latestGroupSequence: Long,
    @ProtoNumber(4) val version: Int,
    @ProtoNumber(5) val checkType: Int,
    @ProtoNumber(6) val flag: FlagInfo,
    @ProtoNumber(7) val language: Int,
    @ProtoNumber(8) val isGetFriendRibbon: Boolean,
    @ProtoNumber(9) val isGetGroupRibbon: Boolean,
    @ProtoNumber(10) val friendMessageTypeFlag: Long,
    @ProtoNumber(11) val messageType: Int,
) {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID(), suspicious: Boolean) =
            TransportPacket.Request.Buffered(
                client = client,
                encryptType = TransportPacket.EncryptType.D2_KEY,
                sequenceID = sequenceID,
                command = "ProfileService.Pb.ReqSystemMsgNew.Group",
                body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                    ProtoBuf.encodeToByteArray(
                        PullGroupSystemMessagesRequest(
                            messageNumber = 100,
                            version = 1000,
                            checkType = 3,
                            friendMessageTypeFlag = 1,
                            messageType = if (suspicious) 2 else 1,
                            flag = FlagInfo(
                                groupKickAdmin = 1,
                                groupHiddenGroup = 1,
                                groupWordingDown = 1,
                                groupGetOfficialAccount = 1,
                                groupGetPayInGroup = 1,
                                friendDiscuss2ManyChat = 1,
                                groupNotAllowJoinGroupInviteNotFriend = 1,
                                friendNeedWaiting = 1,
                                friendUint32NeedAllUnread = 1,
                                groupNeedAutoAdminWording = 1,
                                groupGetTransferGroupFlag = 1,
                                groupGetQuitPayGroupFlag = 1,
                                groupSupportInviteAutoJoin = 1,
                                groupMaskInviteAutoJoin = 1,
                                groupGetDisbandedByAdmin = 1,
                                groupGetC2cInviteJoinGroup = 1,
                                friendGetBusiCard = 1
                            ),
                            isGetFriendRibbon = false,
                            isGetGroupRibbon = false,
                            latestGroupSequence = 0,
                            latestFriendSequence = 0,
                            language = 0
                        )
                    )
                )
            )

    }

    @Serializable
    data class FlagInfo(
        @ProtoNumber(1) val groupKickAdmin: Int,
        @ProtoNumber(2) val groupHiddenGroup: Int,
        @ProtoNumber(3) val groupWordingDown: Int,
        @ProtoNumber(4) val friendGetBusiCard: Int,
        @ProtoNumber(5) val groupGetOfficialAccount: Int,
        @ProtoNumber(6) val groupGetPayInGroup: Int,
        @ProtoNumber(7) val friendDiscuss2ManyChat: Int,
        @ProtoNumber(8) val groupNotAllowJoinGroupInviteNotFriend: Int,
        @ProtoNumber(9) val friendNeedWaiting: Int,
        @ProtoNumber(10) val friendUint32NeedAllUnread: Int,
        @ProtoNumber(11) val groupNeedAutoAdminWording: Int,
        @ProtoNumber(12) val groupGetTransferGroupFlag: Int,
        @ProtoNumber(13) val groupGetQuitPayGroupFlag: Int,
        @ProtoNumber(14) val groupSupportInviteAutoJoin: Int,
        @ProtoNumber(15) val groupMaskInviteAutoJoin: Int,
        @ProtoNumber(16) val groupGetDisbandedByAdmin: Int,
        @ProtoNumber(17) val groupGetC2cInviteJoinGroup: Int,
    )

}