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
import katium.client.qq.network.codec.oidb.writeOidbPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbOidb0x88D

object PullGroupInfoRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        groupCode: Long
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "OidbSvc.0x88d_0",
            body = ByteBufAllocator.DEFAULT.heapBuffer()
                .writeOidbPacket(client, 2189, 0, createRequest(client, groupCode).toByteString())
        )

    fun createRequest(client: QQClient, groupCode: Long): PbOidb0x88D.D88DRequest = PbOidb0x88D.D88DRequest.newBuilder()
        .setAppID(client.clientVersion.appID)
        .addInfo(
            PbOidb0x88D.D88DRequestInfo.newBuilder()
                .setGroupCode(groupCode)
                .setInfo(
                    PbOidb0x88D.D88DGroupInfo.newBuilder()
                        .setGroupOwner(0)
                        .setGroupUin(0)
                        .setGroupCreateTime(0)
                        .setGroupFlag(0)
                        .setGroupMemberMaxNum(0)
                        .setGroupMemberNum(0)
                        .setGroupOption(0)
                        .setGroupLevel(0)
                        .setGroupFace(0)
                        .setGroupName(ByteString.empty())
                        .setGroupMemo(ByteString.empty())
                        .setGroupFingerMemo(ByteString.empty())
                        .setGroupLastMessageTime(0)
                        .setGroupCurrentMessageSequence(0)
                        .setGroupQuestion(ByteString.empty())
                        .setGroupAnswer(ByteString.empty())
                        .setGroupGrade(0)
                        .setActiveMemberNum(0)
                        .setHeadPortraitSeq(0)
                        .setMsgHeadPortrait(PbOidb0x88D.D88DGroupHeaderPortrait.newBuilder())
                        .setStGroupExInfo(PbOidb0x88D.D88DGroupExInfoOnly.newBuilder())
                        .setGroupSecLevel(0)
                        .setCmduinPrivilege(0)
                        .setNoFingerOpenFlag(0)
                        .setNoCodeFingerOpenFlag(0)
                )
        )
        .setPcClientVersion(0)
        .build()
}