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
import katium.core.util.netty.toArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
class RecallMessagesResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    companion object {

        val diagnosis = mapOf(
            154 to "timeout",
            1001 to "no permission"
        )

    }

    lateinit var response: Data
        private set

    var errorMessage: String? = null
        private set

    override fun readBody(input: ByteBuf) {
        response = ProtoBuf.decodeFromByteArray(input.toArray(release = false))

        errorMessage = ""
        response.friends
            .filter { it.result != 2 && it.result != 3 }
            .filter { it.error != null }
            .forEach {
                errorMessage += "friend: result=${it.result}, error=${it.error}, diagnosis=${diagnosis[it.result]}"
            }
        response.friends
            .filter { it.result != 0 }
            .filter { it.error != null }
            .forEach {
                errorMessage += "group: result=${it.result}, error=${it.error}, diagnosis=${diagnosis[it.result]}"
            }
        if (errorMessage!!.isEmpty()) errorMessage = null
    }

    @Serializable
    data class Data(
        @ProtoNumber(1) val friends: Set<FriendResponse> = emptySet(),
        @ProtoNumber(2) val groups: Set<GroupResponse> = emptySet(),
    )

    @Serializable
    data class FriendResponse(
        @ProtoNumber(1) val result: Int,
        @ProtoNumber(2) val error: String? = null,
    )

    @Serializable
    data class GroupResponse(
        @ProtoNumber(1) val result: Int,
        @ProtoNumber(2) val error: String? = null,
    )

}