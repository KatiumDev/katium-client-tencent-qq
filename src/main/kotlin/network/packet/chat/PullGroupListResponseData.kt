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
import katium.client.qq.network.codec.jce.SimpleJceStruct

class PullGroupListResponseData(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var uin: Long by number(0u)
    var troopCount: Short by number(1u)
    var result: Int by number(2u)
    var errorCode: Short by number(3u)
    var cookies: ByteBuf by byteBuf(4u)
    var troopList: MutableSet<SimpleJceStruct> by set(5u)
    var troopListDelete: MutableSet<SimpleJceStruct> by set(6u)
    var troopRank: MutableSet<SimpleJceStruct> by set(7u)
    var favoriteGroup: MutableSet<SimpleJceStruct> by set(8u)
    var troopListExtension: MutableSet<SimpleJceStruct> by set(9u)
    var groupInfoExtension: MutableSet<Long> by set(10u)

    override fun toString() =
        "PullGroupListResponseData(uin=$uin, troopCount=$troopCount, result=$result, errorCode=$errorCode, " +
                "cookies=$cookies, troopList=$troopList, troopListDelete=$troopListDelete, troopRank=$troopRank, " +
                "favoriteGroup=$favoriteGroup, troopListExtension=$troopListExtension, " +
                "groupInfoExtension=$groupInfoExtension)"

    class Troop(other: SimpleJceStruct) : SimpleJceStruct(other) {

        constructor() : this(SimpleJceStruct())
        constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

        var uin: Long by number(0u)
        var groupCode: Long by number(1u)
        var groupName: String by string(4u)
        var groupOwnerUin: Long by number(23u)

    }

}