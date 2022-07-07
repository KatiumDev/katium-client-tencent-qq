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

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.review.group.QQGroupInvitationMessage
import katium.client.qq.review.group.QQJoinGroupRequestMessage
import katium.core.review.ReviewMessage
import katium.core.util.netty.toArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
class PullGroupSystemMessagesResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    var messages: List<ReviewMessage> = emptyList()
        private set

    override fun readBody(input: ByteBuf) {
        val payload = ProtoBuf.decodeFromByteArray<Data>(input.toArray(false))
        val reviewMessages = mutableListOf<ReviewMessage>()
        payload.groupMessages.forEach { message ->
            when (message.message.subType) {
                1, 2 -> {
                    when (message.message.groupMessageType) {
                        1 -> reviewMessages += QQJoinGroupRequestMessage(client, message)
                        2 -> reviewMessages += QQGroupInvitationMessage(client, message)
                        22 -> TODO("https://cs.github.com/Mrs4s/MiraiGo/blob/master/client/system_msg.go#L230")
                        else -> throw UnsupportedOperationException("Unknown system group message type: ${message.message.groupMessageType} $message")
                    }
                }
                3 -> {}
                5 -> {} // 自身状态变更(管理员/加群退群)
                else -> throw UnsupportedOperationException("Unknown system message sub-type: ${message.message.subType} $message")
            }
        }
        messages = reviewMessages.toList()
    }

    @Serializable
    data class Data(
        @ProtoNumber(1) val head: PushHeader? = null,
        @ProtoNumber(2) val unreadFriendsCount: Int? = null,
        @ProtoNumber(3) val unreadGroupsCount: Int? = null,
        @ProtoNumber(4) val latestFriendSequence: Long? = null,
        @ProtoNumber(5) val latestGroupSequence: Long? = null,
        @ProtoNumber(6) val followingFriendSequence: Long? = null,
        @ProtoNumber(7) val followingGroupSequence: Long? = null,
        @ProtoNumber(9) val friendMessages: Set<StructMessage> = emptySet(),
        @ProtoNumber(10) val groupMessages: Set<StructMessage> = emptySet(),
        @ProtoNumber(11) val ribbonFriend: StructMessage? = null,
        @ProtoNumber(12) val ribbonGroup: StructMessage? = null,
        @ProtoNumber(13) val messageDisplay: String? = null,
        @ProtoNumber(14) val groupMessageDisplay: String? = null,
        @ProtoNumber(15) val over: Int? = null,
        @ProtoNumber(20) val checkType: Int? = null,
        @ProtoNumber(100) val gameNick: String? = null,
        @ProtoNumber(101) val undecidForQim: ByteArray? = null,
        @ProtoNumber(102) val unReadCount3: Int? = null,
    )

    @Serializable
    data class PushHeader(
        @ProtoNumber(1) val result: Int? = null,
        @ProtoNumber(2) val message: String? = null,
    )

    @Serializable
    data class StructMessage(
        @ProtoNumber(1) val version: Int? = null,
        @ProtoNumber(2) val messageType: Int? = null,
        @ProtoNumber(3) val messageSequence: Long,
        @ProtoNumber(4) val messageTime: Long? = null,
        @ProtoNumber(5) val requesterUin: Long,
        @ProtoNumber(6) val unreadFlag: Int? = null,
        @ProtoNumber(50) val message: SystemMessage,
    )

    @Serializable
    data class SystemMessage(
        @ProtoNumber(1) val subType: Int,
        @ProtoNumber(2) val title: String? = null,
        @ProtoNumber(3) val description: String? = null,
        @ProtoNumber(4) val addition: String? = null,
        @ProtoNumber(5) val source: String? = null,
        @ProtoNumber(6) val decided: String? = null,
        @ProtoNumber(7) val sourceID: Int? = null,
        @ProtoNumber(8) val subSourceID: Int? = null,
        @ProtoNumber(9) val actions: Set<SystemMsgAction> = emptySet(),
        @ProtoNumber(10) val groupCode: Long,
        @ProtoNumber(11) val actionUin: Long,
        @ProtoNumber(12) val groupMessageType: Int? = null,
        @ProtoNumber(13) val groupInviterRole: Int? = null,
        @ProtoNumber(14) val friendInfo: FriendInfo? = null,
        @ProtoNumber(15) val groupInfo: SGroupInfo? = null,
        @ProtoNumber(16) val actorUin: Long? = null,
        @ProtoNumber(17) val actorDescription: String? = null,
        @ProtoNumber(18) val additionList: String? = null,
        @ProtoNumber(19) val relation: Int? = null,
        @ProtoNumber(20) val requestSubType: Int? = null,
        @ProtoNumber(21) val cloneUin: Long? = null,
        @ProtoNumber(22) val discussUin: Long? = null,
        @ProtoNumber(23) val eimGroupId: Long? = null,
        @ProtoNumber(24) val invitationInfo: InvitationInfo? = null,
        @ProtoNumber(25) val payGroupInfo: PayGroupInfo? = null,
        @ProtoNumber(26) val sourceFlag: Int? = null,
        @ProtoNumber(27) val gameNick: ByteArray? = null,
        @ProtoNumber(28) val gameMsg: ByteArray? = null,
        @ProtoNumber(29) val groupFlagExt3: Int? = null,
        @ProtoNumber(30) val groupOwnerUin: Long? = null,
        @ProtoNumber(31) val doubtFlag: Int? = null,
        @ProtoNumber(32) val warningTips: ByteArray,
        @ProtoNumber(33) val nameMore: ByteArray? = null,
        @ProtoNumber(50) val requestUinFaceID: Int? = null,
        @ProtoNumber(51) val requestUinNick: String? = null,
        @ProtoNumber(52) val groupName: String? = null,
        @ProtoNumber(53) val actionUinNick: String? = null,
        @ProtoNumber(54) val messageQna: String? = null,
        @ProtoNumber(55) val messageDetail: String? = null,
        @ProtoNumber(57) val groupExtFlag: Int? = null,
        @ProtoNumber(58) val actorUinNick: String? = null,
        @ProtoNumber(59) val pictureUrl: String? = null,
        @ProtoNumber(60) val cloneUinNick: String? = null,
        @ProtoNumber(61) val requestUinBusinessCard: String? = null,
        @ProtoNumber(63) val eimGroupIdName: String? = null,
        @ProtoNumber(64) val requestUinPreRemark: String? = null,
        @ProtoNumber(65) val actionUinQqNick: String? = null,
        @ProtoNumber(66) val actionUinRemark: String? = null,
        @ProtoNumber(67) val requestUinGender: Int? = null,
        @ProtoNumber(68) val requestUinAge: Int? = null,
        @ProtoNumber(69) val c2cInviteJoinGroupFlag: Int? = null,
        @ProtoNumber(101) val cardSwitch: Int? = null,
    )

    @Serializable
    data class SystemMsgAction(
        @ProtoNumber(1) val name: String? = null,
        @ProtoNumber(2) val result: String? = null,
        @ProtoNumber(3) val action: Int? = null,
        @ProtoNumber(4) val actionInfo: SystemMsgActionInfo? = null,
        @ProtoNumber(5) val detailName: String? = null,
    )

    @Serializable
    data class SystemMsgActionInfo(
        @ProtoNumber(1) val type: Int? = null,
        @ProtoNumber(2) val groupCode: Long? = null,
        @ProtoNumber(3) val sig: ByteArray? = null,
        @ProtoNumber(50) val message: String? = null,
        @ProtoNumber(51) val groupID: Int? = null,
        @ProtoNumber(52) val remark: String? = null,
        @ProtoNumber(53) val blacklist: Boolean? = null,
        @ProtoNumber(54) val addFrdSNInfo: AddFrdSNInfo? = null,
    )

    @Serializable
    data class AddFrdSNInfo(
        @ProtoNumber(1) val notSeeDynamic: Int? = null,
        @ProtoNumber(2) val setSn: Int? = null,
    )

    @Serializable
    data class FriendInfo(
        @ProtoNumber(1) val jointFriend: String? = null,
        @ProtoNumber(2) val blacklist: String? = null,
    )

    @Serializable
    data class SGroupInfo(
        @ProtoNumber(1) val groupAuthType: Int? = null,
        @ProtoNumber(2) val displayAction: Int? = null,
        @ProtoNumber(3) val alert: String? = null,
        @ProtoNumber(4) val detailAlert: String? = null,
        @ProtoNumber(5) val otherAdminDone: String? = null,
        @ProtoNumber(6) val appPrivilegeFlag: Int? = null,
    )

    @Serializable
    data class InvitationInfo(
        @ProtoNumber(1) val sourceType: Int? = null,
        @ProtoNumber(2) val sourceCode: Long? = null,
        @ProtoNumber(3) val waitState: Int? = null,
    )

    @Serializable
    data class PayGroupInfo(
        @ProtoNumber(1) val joinGroupTime: Long? = null,
        @ProtoNumber(2) val quitGroupTime: Long? = null,
    )

}