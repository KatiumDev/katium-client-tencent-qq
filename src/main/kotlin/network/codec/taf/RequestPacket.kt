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
package katium.client.qq.network.codec.taf

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
