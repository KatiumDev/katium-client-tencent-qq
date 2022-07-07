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
import katium.core.util.netty.toArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import java.net.InetSocketAddress

@OptIn(ExperimentalSerializationApi::class)
class QueryFriendImageResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: Data
        private set

    lateinit var result: ImageUploadResult
        private set

    override fun readBody(input: ByteBuf) {
        response = ProtoBuf.decodeFromByteArray(input.toArray(release = false))

        result = if (response.failMessage != null) {
            ImageUploadResult(message = "failMessage: ${String(response.failMessage!!)}")
        } else if (response.subCommand != 1) {
            ImageUploadResult(message = "subCommand: ${response.subCommand}")
        } else if (response.uploadResponses.isEmpty()) {
            ImageUploadResult(message = "no upload response")
        } else {
            val uploadResponse = response.uploadResponses.first()
            if (uploadResponse.result != 0) {
                ImageUploadResult(message = "result: ${uploadResponse.result}, failMessage: ${uploadResponse.failMessage}")
            } else if (uploadResponse.fileExists) {
                ImageUploadResult(
                    isExists = true,
                    resourceKey = String(uploadResponse.uploadResourceID!!),
                    contentUrl = if (uploadResponse.originalDownPara != null)
                        "https://c2cpicdw.qpic.cn/${String(uploadResponse.originalDownPara)}"
                    else null
                )
            } else {
                ImageUploadResult(
                    isExists = false,
                    resourceKey = String(uploadResponse.uploadResourceID!!),
                    uploadServers = uploadResponse.uploadIPs.mapIndexed { index, ip ->
                        InetSocketAddress(
                            Highway.decodeIPv4(ip),
                            uploadResponse.uploadPorts[index]
                        )
                    },
                    uploadKey = uploadResponse.uploadUkey
                )
            }
        }
    }

    @Serializable
    data class Data(
        @ProtoNumber(1) val subCommand: Int? = null,
        @ProtoNumber(2) val uploadResponses: Set<C352UploadResponse> = emptySet(),
        // @ProtoNumber(3) val getimgUrlRsp: Set<GetImgUrlRsp> = emptySet(),
        @ProtoNumber(4) val newBigchan: Boolean? = null,
        // @ProtoNumber(5) val delImgRsp: Set<DelImgRsp> = emptySet(),
        @ProtoNumber(10) val failMessage: ByteArray? = null,
    )

    @Serializable
    data class C352UploadResponse(
        @ProtoNumber(1) val fileID: Long? = null,
        @ProtoNumber(2) val clientIP: Int? = null,
        @ProtoNumber(3) val result: Int,
        @ProtoNumber(4) val failMessage: ByteArray? = null,
        @ProtoNumber(5) val fileExists: Boolean,
        // @ProtoNumber(6) val imgInfo: ImgInfo? = null,
        @ProtoNumber(7) val uploadIPs: List<Int> = emptyList(),
        @ProtoNumber(8) val uploadPorts: List<Int> = emptyList(),
        @ProtoNumber(9) val uploadUkey: ByteArray? = null,
        @ProtoNumber(10) val uploadResourceID: ByteArray? = null,
        @ProtoNumber(11) val uploadUuid: ByteArray? = null,
        @ProtoNumber(12) val upOffset: Long? = null,
        @ProtoNumber(13) val blockSize: Long? = null,
        @ProtoNumber(14) val encryptToIP: ByteArray? = null,
        @ProtoNumber(15) val roamdays: Int? = null,
        // @ProtoNumber(26) val upIp6: List<IPv6Info> = emptyList(),
        @ProtoNumber(27) val clientIP6: ByteArray? = null,
        @ProtoNumber(60) val thumbDownPara: ByteArray? = null,
        @ProtoNumber(61) val originalDownPara: ByteArray? = null,
        @ProtoNumber(62) val downDomain: ByteArray? = null,
        @ProtoNumber(64) val bigDownPara: ByteArray? = null,
        @ProtoNumber(65) val bigThumbDownPara: ByteArray? = null,
        @ProtoNumber(66) val httpsUrlFlag: Int? = null,
        // @ProtoNumber(1001) val info4Busi: TryUpInfo4Busi? = null,
    )

}