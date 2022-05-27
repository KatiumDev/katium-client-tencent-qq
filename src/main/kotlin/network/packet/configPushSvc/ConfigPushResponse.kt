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
package katium.client.qq.network.packet.configPushSvc

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.taf.RequestDataV3
import katium.client.qq.network.codec.taf.RequestPacket
import katium.client.qq.network.codec.taf.wrapUniRequestData

object ConfigPushResponse {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocSequenceID(),
        type: Int,
        buffer: ByteBuf,
        actionSequenceID: Long
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "ConfigPushSvc.PushResp",
            body = createRequestPacket(client, type, buffer, actionSequenceID).dump()
        )

    fun createRequestPacket(client: QQClient, type: Int, buffer: ByteBuf, sequenceID: Long) = RequestPacket(
        version = 3,
        servantName = "QQService.ConfigPushSvc.MainServant",
        functionName = "PushResp",
        buffer = RequestDataV3(
            "PushResp" to ConfigPushAction().apply {

            }.dump().wrapUniRequestData()
        ).dump()
    )

}