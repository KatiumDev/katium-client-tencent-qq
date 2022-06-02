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

import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbSystemMessages
import katium.core.util.netty.heapBuffer

object PullGroupSystemMessagesRequest {

    fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID(), suspicious: Boolean) =
        TransportPacket.Request.Buffered(
            client = client,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "ProfileService.Pb.ReqSystemMsgNew.Group",
            body = ByteBufAllocator.DEFAULT.heapBuffer(
                PbSystemMessages.PullSystemMessagesRequest.newBuilder()
                    .setMessageType(100)
                    .setVersion(1000)
                    .setCheckType(3)
                    .setFlag(
                        PbSystemMessages.FlagInfo.newBuilder()
                            .setGroupKickAdmin(1)
                            .setGroupHiddenGroup(1)
                            .setGroupWordingDown(1)
                            .setGroupGetOfficialAccount(1)
                            .setGroupGetPayInGroup(1)
                            .setFriendDiscuss2ManyChat(1)
                            .setGroupNotAllowJoinGroupInviteNotFriend(1)
                            .setFriendNeedWaiting(1)
                            .setFriendUint32NeedAllUnread(1)
                            .setGroupNeedAutoAdminWording(1)
                            .setGroupGetTransferGroupFlag(1)
                            .setGroupGetQuitPayGroupFlag(1)
                            .setGroupSupportInviteAutoJoin(1)
                            .setGroupMaskInviteAutoJoin(1)
                            .setGroupGetDisbandedByAdmin(1)
                            .setGroupGetC2CInviteJoinGroup(1)
                    )
                    .setFriendMessageTypeFlag(1)
                    .setMessageType(if (suspicious) 2 else 1).build().toByteArray()
            )
        )

}