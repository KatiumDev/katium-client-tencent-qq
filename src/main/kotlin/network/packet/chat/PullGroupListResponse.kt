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
import katium.client.qq.network.codec.jce.readJceStruct
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.taf.RequestDataV3
import katium.client.qq.network.codec.taf.RequestPacket
import katium.client.qq.network.codec.taf.unwrapUniRequestData

class PullGroupListResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: PullGroupListResponseData
        private set

    override fun close() {
        super.close()
        response.release()
    }

    override fun readBody(input: ByteBuf) {
        RequestPacket(input.readJceStruct()).use {
            RequestDataV3(it.buffer.readJceStruct()).use { data ->
                response =
                    PullGroupListResponseData(data["GetTroopListRespV2"]!!.unwrapUniRequestData().readJceStruct())
            }
        }
    }

}