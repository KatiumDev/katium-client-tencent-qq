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
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMessagePackets
import katium.core.util.netty.toArray

class RecallMessagesResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    companion object {

        val diagnosis = mapOf(
            154 to "timeout",
            1001 to "no permission"
        )

    }

    lateinit var response: PbMessagePackets.RecallMessagesResponse
        private set

    var errorMessage: String? = null
        private set

    override fun readBody(input: ByteBuf) {
        response = PbMessagePackets.RecallMessagesResponse.parseFrom(input.toArray(release = false))

        errorMessage = ""
        response.friendList
            .filter { it.result != 2 && it.result != 3 }
            .filter(PbMessagePackets.RecallFriendMessagesResponse::hasError)
            .forEach {
                errorMessage += "friend: result=${it.result}, error=${it.error}, diagnosis=${diagnosis[it.result]}"
            }
        response.groupList
            .filter { it.result != 0 }
            .filter(PbMessagePackets.RecallGroupMessagesResponse::hasError)
            .forEach {
                errorMessage += "group: result=${it.result}, error=${it.error}, diagnosis=${diagnosis[it.result]}"
            }
        if (errorMessage!!.isEmpty()) errorMessage = null
    }

}