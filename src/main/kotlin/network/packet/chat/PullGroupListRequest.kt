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
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.jce.SimpleJceStruct
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.taf.RequestDataV3
import katium.client.qq.network.codec.taf.RequestPacket
import katium.client.qq.network.codec.taf.wrapUniRequestData

class PullGroupListRequest(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocPacketSequenceID(), cookies: ByteBuf) =
            TransportPacket.Request.Buffered(
                client = client,
                type = TransportPacket.Type.LOGIN,
                encryptType = TransportPacket.EncryptType.D2_KEY,
                sequenceID = sequenceID,
                command = "friendlist.GetTroopListReqV2",
                body = createRequestPacket(client, cookies).dump()
            )

        fun createRequestPacket(client: QQClient, cookies: ByteBuf) =
            RequestPacket(
                version = 3,
                servantName = "mqq.IMService.FriendListServiceServantObj",
                functionName = "GetTroopListReqV2Simplify",
                buffer = RequestDataV3(
                    "GetTroopListReqV2Simplify" to PullGroupListRequest().apply {
                        uin = client.uin
                        this.cookies = cookies
                        cookies.retain()
                    }.dump().wrapUniRequestData()
                ).dump()
            ).apply {
                packetType = 0x003
                requestID = 1921334514
            }

    }

    var uin: Long by number(0u)
    var getMSFMessagesFlag: Byte by number(1u, 1)
    var cookies: ByteBuf by byteBuf(2u)
    var groupInfo: MutableSet<Long> by set(3u)
    var groupFlagExtension: Byte by number(4u, 1)
    var version: Int by number(5u, 7)
    var companyID: Long by number(6u, 0)
    var versionNumber: Long by number(7u, 1)
    var getLongGroupName: Byte by number(8u, 1)

    override fun toString() = "PullGroupListRequest(uin=$uin, getMSFMessagesFlag=$getMSFMessagesFlag, " +
            "cookies=$cookies, groupInfo=$groupInfo, groupFlagExtension=$groupFlagExtension, version=$version, " +
            "companyID=$companyID, versionNumber=$versionNumber, getLongGroupName=$getLongGroupName)"

}