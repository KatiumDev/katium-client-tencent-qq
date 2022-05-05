/*
 * Katium Client Tencent QQ: Tencent QQ protocol implementation for Katium
 * Copyright (C) 2022  Katium Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package katium.client.qq.network.codec.struct

import io.netty.buffer.ByteBuf
import katium.client.qq.network.codec.jce.SimpleJceStruct

class RequestPacket(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var version: Short by number(1u)
    var packetType: Byte by number(2u)
    var messageType: Int by number(3u)
    var requestID: Int by number(4u)
    var servantName: String by string(5u)
    var functionName: String by string(6u)
    var buffer: ByteBuf by byteBuf(7u)
    var timeout: Int by number(8u)
    var context: MutableMap<String, String> by map(9u)
    var status: MutableMap<String, String> by map(10u)

    override fun release() {
        super.release()
        buffer.release()
    }

    override fun toString() =
        "RequestPacket(version=$version, packetType=$packetType, messageType=$messageType, requestID=$requestID, " +
                "servantName='$servantName', functionName='$functionName', buffer=$buffer, timeout=$timeout, " +
                "context=$context, status=$status)"

}

fun RequestPacket(version: Short, servantName: String, functionName: String, buffer: ByteBuf) =
    RequestPacket().apply {
        this.version = version
        this.servantName = servantName
        this.functionName = functionName
        this.buffer = buffer
    }
