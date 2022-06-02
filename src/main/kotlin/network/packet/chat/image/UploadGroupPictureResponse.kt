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
package katium.client.qq.network.packet.chat.image

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.highway.Highway
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbCmd0x388
import katium.core.util.netty.toArray
import java.net.InetSocketAddress

class UploadGroupPictureResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: PbCmd0x388.C388Response
        private set

    lateinit var result: ImageUploadResult
        private set

    override fun readBody(input: ByteBuf) {
        response = PbCmd0x388.C388Response.parseFrom(input.toArray(release = false))

        result = if (response.subCommand != 1) {
            ImageUploadResult(message = "subCommand: ${response.subCommand}")
        } else if (response.uploadResponseCount == 0) {
            ImageUploadResult(message = "no upload response")
        } else {
            val uploadResponse = response.uploadResponseList.first()
            if (uploadResponse.result != 0) {
                ImageUploadResult(message = "result: ${uploadResponse.result}, failMessage: ${uploadResponse.failMessage}")
            } else if (uploadResponse.fileExists) {
                ImageUploadResult(
                    isExists = true,
                    resourceKey = uploadResponse.fileID2.toString()
                )
            } else {
                ImageUploadResult(
                    isExists = false,
                    resourceKey = uploadResponse.fileID2.toString(),
                    uploadServers = uploadResponse.uploadIPList.mapIndexed { index, ip ->
                        InetSocketAddress(
                            Highway.decodeIP(ip),
                            uploadResponse.getUploadPort(index)
                        )
                    },
                    uploadKey = uploadResponse.uploadUkey
                )
            }
        }
    }

}