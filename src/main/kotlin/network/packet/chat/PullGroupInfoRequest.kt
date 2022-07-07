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
import katium.client.qq.network.codec.oidb.writeOidbPacket
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbGroupInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PullGroupInfoRequest(
    @ProtoNumber(1) val appID: Int? = null,
    @ProtoNumber(2) val info: Set<Info> = emptySet(),
    @ProtoNumber(3) val pcClientVersion: Int? = null,
) {

    companion object {

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
                body = PooledByteBufAllocator.DEFAULT.heapBuffer()
                    .writeOidbPacket(client, 2189, 0, ProtoBuf.encodeToByteArray(createRequest(client, groupCode)))
            )

        fun createRequest(client: QQClient, groupCode: Long) = PullGroupInfoRequest(
            appID = client.version.appID,
            info = setOf(
                Info(
                    groupCode = groupCode,
                    info = PbGroupInfo(
                        groupOwner = 0,
                        groupUin = 0,
                        groupCreateTime = 0,
                        groupFlag = 0,
                        groupMemberMaxNum = 0,
                        groupMemberNum = 0,
                        groupOption = 0,
                        groupLevel = 0,
                        groupFace = 0,
                        groupName = ByteArray(0),
                        groupMemo = ByteArray(0),
                        groupFingerMemo = ByteArray(0),
                        groupLastMessageTime = 0,
                        groupCurrentMessageSequence = 0,
                        groupQuestion = ByteArray(0),
                        groupAnswer = ByteArray(0),
                        groupGrade = 0,
                        activeMemberNum = 0,
                        headPortraitSeq = 0,
                        msgHeadPortrait = PbGroupInfo.GroupHeaderPortrait(),
                        stGroupExInfo = PbGroupInfo.GroupExInfoOnly(),
                        groupSecLevel = 0,
                        cmduinPrivilege = 0,
                        noFingerOpenFlag = 0,
                        noCodeFingerOpenFlag = 0
                    )
                )
            ),
            pcClientVersion = 0
        )

    }

    @Serializable
    data class Info(
        @ProtoNumber(1) val groupCode: Long? = null,
        @ProtoNumber(2) val info: PbGroupInfo? = null,
        @ProtoNumber(3) val lastGetGroupNameTime: Int? = null,
    )

}