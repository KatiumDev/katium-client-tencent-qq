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
package katium.client.qq.network.packet.review

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbSystemMessages
import katium.client.qq.review.group.QQGroupInvitationMessage
import katium.client.qq.review.group.QQJoinGroupRequestMessage
import katium.core.review.ReviewMessage
import katium.core.util.netty.toArray

class PullGroupSystemMessagesResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    var messages: List<ReviewMessage> = emptyList()
        private set

    override fun readBody(input: ByteBuf) {
        val payload = PbSystemMessages.PullSystemMessagesResponse.parseFrom(input.toArray(false))
        val reviewMessages = mutableListOf<ReviewMessage>()
        payload.groupMessagesList.forEach { message ->
            when (message.message.subType) {
                1, 2 -> {
                    when (message.message.groupMessageType) {
                        1 -> reviewMessages += QQJoinGroupRequestMessage(client, message)
                        2 -> reviewMessages += QQGroupInvitationMessage(client, message)
                        22 -> TODO("https://cs.github.com/Mrs4s/MiraiGo/blob/master/client/system_msg.go#L230")
                        else -> throw UnsupportedOperationException("Unknown system group message type: ${message.message.groupMessageType} $message")
                    }
                }
                3 -> {}
                5 -> {} // 自身状态变更(管理员/加群退群)
                else -> throw UnsupportedOperationException("Unknown system message sub-type: ${message.message.subType} $message")
            }
        }
        messages = reviewMessages.toList()
    }

}