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

import katium.client.qq.network.codec.jce.SimpleJceStruct

class PullFriendListResponseData(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var requestType: Int by number(0u)
    var reflush: Byte by number(1u)
    var uin: Long by number(2u)
    var friendStartIndex: Short by number(3u)
    var getFriendCount: Short by number(4u)
    var totalFriendsCount: Short by number(5u)
    var friendCount: Short by number(6u)
    var friends: MutableList<SimpleJceStruct> by list(7u)
    var groupID: Byte by number(8u)
    var getGroupInfo: Byte by number(9u)
    var groupStartIndex: Byte by number(10u)
    var getGroupCount: Byte by number(11u)
    var totalGroupsCount: Short by number(12u)
    var groupCount: Byte by number(13u)
    var groups: MutableList<SimpleJceStruct> by list(14u)
    var result: Int by number(15u)
    var errorCode: Short by number(16u)
    var onlineFriendCount: Short by number(17u)
    var serverTime: Long by number(18u)
    var qqOnlineCount:Short by number(19u)
    var msfGroupInfo: MutableList<SimpleJceStruct> by list(20u)
    var responseType: Byte by number(21u)
    var hasOtherResponseFlag: Byte by number(22u)
    var selfInfo: Friend by struct(23u, Friend::class)
    var showPCIcon: Byte by number(24u)
    var getExtensionSnsResponseCode: Byte by number(25u)
    var subSrvResponseCode: SimpleJceStruct by struct(26u, SimpleJceStruct::class)

    override fun release() {
        super.release()
        friends.forEach(SimpleJceStruct::release)
        groups.forEach(SimpleJceStruct::release)
        msfGroupInfo.forEach(SimpleJceStruct::release)
        subSrvResponseCode.release()
    }

    override fun toString() = "PullFriendListResponseData(requestType=$requestType, reflush=$reflush, " +
            "uin=$uin, friendStartIndex=$friendStartIndex, getFriendCount=$getFriendCount, " +
            "totalFriendsCount=$totalFriendsCount, friendCount=$friendCount, friends=$friends, " +
            "groupID=$groupID, getGroupInfo=$getGroupInfo, groupStartIndex=$groupStartIndex, " +
            "getGroupCount=$getGroupCount, totalGroupsCount=$totalGroupsCount, groupCount=$groupCount, " +
            "groups=$groups, result=$result, errorCode=$errorCode, onlineFriendCount=$onlineFriendCount, " +
            "serverTime=$serverTime, qqOnlineCount=$qqOnlineCount, msfGroupInfo=$msfGroupInfo, " +
            "responseType=$responseType, hasOtherResponseFlag=$hasOtherResponseFlag, selfInfo=$selfInfo, " +
            "showPCIcon=$showPCIcon, getExtensionSnsResponseCode=$getExtensionSnsResponseCode, " +
            "subSrvResponseCode=$subSrvResponseCode)"

    class Friend(other: SimpleJceStruct) : SimpleJceStruct(other) {

        constructor() : this(SimpleJceStruct())
        constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

        var uin: Long by number(0u)
        var faceID: Short by number(2u)
        var remark: String by string(3u)
        var nickName: String by string(14u)

    }

}