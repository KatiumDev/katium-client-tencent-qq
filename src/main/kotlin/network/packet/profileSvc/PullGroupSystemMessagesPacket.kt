package katium.client.qq.network.packet.profileSvc

import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbSystemMessage
import katium.core.util.netty.heapBuffer

class PullGroupSystemMessagesPacket {

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocSequenceID(), suspicious: Boolean) =
            TransportPacket.Request.Buffered(
                client = client,
                encryptType = TransportPacket.EncryptType.D2_KEY,
                sequenceID = sequenceID,
                command = "ProfileService.Pb.ReqSystemMsgNew.Group",
                body = ByteBufAllocator.DEFAULT.heapBuffer(
                    PbSystemMessage.PullSystemMessages.newBuilder()
                        .setMessageType(100)
                        .setVersion(1000)
                        .setCheckType(3)
                        .setFlag(
                            PbSystemMessage.FlagInfo.newBuilder()
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

}