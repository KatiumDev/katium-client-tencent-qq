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

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.jce.SimpleJceStruct
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.taf.RequestDataV3
import katium.client.qq.network.codec.taf.RequestPacket
import katium.client.qq.network.codec.taf.wrapUniRequestData
import katium.client.qq.network.pb.PbD50
import katium.core.util.netty.heapBuffer

class PullFriendListRequest(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    companion object {

        fun create(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            friends: Pair<Short, Short>,
            groups: Pair<Short, Short>
        ) =
            TransportPacket.Request.Buffered(
                client = client,
                type = TransportPacket.Type.LOGIN,
                encryptType = TransportPacket.EncryptType.D2_KEY,
                sequenceID = sequenceID,
                command = "friendlist.getFriendGroupList",
                body = createRequestPacket(client, friends, groups).dump()
            )

        fun createRequestPacket(client: QQClient, friends: Pair<Short, Short>, groups: Pair<Short, Short>) =
            RequestPacket(
                version = 3,
                servantName = "mqq.IMService.FriendListServiceServantObj",
                functionName = "GetFriendListReq",
                buffer = RequestDataV3(
                    "FL" to PullFriendListRequest().apply {
                        reflush = if (friends.first <= 0) 0 else 1
                        uin = client.uin
                        friendStartIndex = friends.first
                        friendCount = friends.second
                        getGroupInfo = if (groups.second <= 0) 0 else 1
                        groupStartIndex = groups.first.toByte()
                        groupCount = groups.second.toByte()
                        d50 = ByteBufAllocator.DEFAULT.heapBuffer(
                            PbD50.D50Request.newBuilder()
                                .setAppID(1002)
                                .setMusicSwitch(1)
                                .setMutualMarkAlienation(1)
                                .setKsingSwitch(1)
                                .setMutualMarkLbsshare(1)
                                .build()
                                .toByteArray()
                        )
                    }.dump().wrapUniRequestData()
                ).dump()
            ).apply {
                packetType = 0x003
                requestID = 1921334514
            }

    }

    var requestType: Int by number(0u, 3)
    var reflush: Byte by number(1u)
    var uin: Long by number(2u)
    var friendStartIndex: Short by number(3u)
    var friendCount: Short by number(4u)
    var groupID: Byte by number(5u, 0)
    var getGroupInfo: Byte by number(6u)
    var groupStartIndex: Byte by number(7u)
    var groupCount: Byte by number(8u)
    var getMSFGroup: Byte by number(9u, 0)
    var showTermType: Byte by number(10u, 1)
    var version: Long by number(11u, 27)
    var uinList: MutableSet<Long> by set(12u)
    var appType: Int by number(13u, 0)
    var getDOVID: Byte by number(14u, 0)
    var getBothFlag: Byte by number(15u, 0)
    var d50: ByteBuf by byteBuf(16u)
    var d6b: ByteBuf by byteBuf(17u)
    var snsTypeList: MutableList<Long> by field(18u) { mutableListOf(13580L, 13581L, 13582L) }

    override fun release() {
        super.release()
        d50.release()
        d6b.release()
    }

    override fun toString() =
        "PullFriendListRequest(requestType=$requestType, reflush=$reflush, uin=$uin, " +
                "friendStartIndex=$friendStartIndex, friendCount=$friendCount, " +
                "groupID=$groupID, getGroupInfo=$getGroupInfo, groupStartIndex=$groupStartIndex, " +
                "groupCount=$groupCount, getMSFGroup=$getMSFGroup, showTermType=$showTermType, " +
                "version=$version, uinList=$uinList, appType=$appType, getDOVID=$getDOVID, " +
                "getBothFlag=$getBothFlag, d50=$d50, d6b=$d6b, snsTypeList=$snsTypeList)"

}