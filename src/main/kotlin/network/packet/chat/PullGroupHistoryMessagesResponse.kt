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
import katium.client.qq.message.QQMessage
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.message.parser.GroupMessageParser
import katium.client.qq.network.pb.PbMessagePackets
import katium.core.util.netty.toArray
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking

class PullGroupHistoryMessagesResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: PbMessagePackets.PullGroupHistoryMessagesResponse
        private set

    var errorMessage: String? = null
        private set

    override fun readBody(input: ByteBuf) {
        response = PbMessagePackets.PullGroupHistoryMessagesResponse.parseFrom(input.toArray(release = false))

        if (response.result != 0) {
            errorMessage = "result: ${response.result}, errorMessage: ${response.error}"
        }
    }

}