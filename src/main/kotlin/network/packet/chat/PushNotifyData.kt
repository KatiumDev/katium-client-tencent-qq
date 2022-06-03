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

class PushNotifyData(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var uin: Long by number(0u)
    var type: Byte by number(1u)
    var service: String by string(2u)
    var command: String by string(3u)
    var notifyCookie: ByteBuf by byteBuf(4u)
    var messageType: Int by number(5u)
    var userActive: Int by number(6u)
    var generalFlag: Int by number(7u)
    var boundUin: Long by number(8u)
    var messageInfo: SimpleJceStruct by field(9u, ::SimpleJceStruct)
    var messageControl: String by string(10u)
    var serverBuffer: ByteBuf by byteBuf(11u)
    var pingFlag: Long by number(12u)
    var serverIP: Int by number(13u)

    override fun release() {
        super.release()
        notifyCookie.release()
        messageInfo.release()
        serverBuffer.release()
    }

    override fun toString() = "PushNotifyData(uin=$uin, type=$type, service='$service', command='$command', " +
            "notifyCookie=$notifyCookie, messageType=$messageType, userActive=$userActive, generalFlag=$generalFlag, " +
            "boundUin=$boundUin, messageInfo=$messageInfo, messageControl='$messageControl', serverBuffer=$serverBuffer, " +
            "pingFlag=$pingFlag, serverIP=$serverIP)"

}