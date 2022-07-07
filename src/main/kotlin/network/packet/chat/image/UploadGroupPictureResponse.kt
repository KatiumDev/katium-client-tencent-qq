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
class UploadGroupPictureResponse(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    lateinit var response: Data
        private set

    lateinit var result: ImageUploadResult
        private set

    override fun readBody(input: ByteBuf) {
        response = ProtoBuf.decodeFromByteArray(input.toArray(release = false))

        result = if (response.subCommand != 1) {
            ImageUploadResult(message = "wrong subCommand: ${response.subCommand}")
        } else if (response.uploadResponses.isEmpty()) {
            ImageUploadResult(message = "no upload response")
        } else {
            val uploadResponse = response.uploadResponses.first()
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
        @ProtoNumber(1) val clientIP: Int? = null,
        @ProtoNumber(2) val subCommand: Int? = null,
        @ProtoNumber(3) val uploadResponses: Set<C388UploadResponse> = emptySet(),
        /*@ProtoNumber(4) val getimgUrlRsp: Set<GetImgUrlRsp> = emptySet(),
        @ProtoNumber(5) val tryupPttRsp: Set<TryUpPttRsp> = emptySet(),
        @ProtoNumber(6) val getpttUrlRsp: Set<GetPttUrlRsp> = emptySet(),
        @ProtoNumber(7) val delImgRsp: Set<DelImgRsp> = emptySet(),*/
    )

    @Serializable
    data class C388UploadResponse(
        @ProtoNumber(1) val fileID1: Long? = null,
        @ProtoNumber(2) val result: Int,
        @ProtoNumber(3) val failMessage: ByteArray? = null,
        @ProtoNumber(4) val fileExists: Boolean,
        @ProtoNumber(5) val imgInfo: ImageInfo? = null,
        @ProtoNumber(6) val uploadIPs: List<Int> = emptyList(),
        @ProtoNumber(7) val uploadPorts: List<Int> = emptyList(),
        @ProtoNumber(8) val uploadUkey: ByteArray? = null,
        @ProtoNumber(9) val fileID2: Long,
        @ProtoNumber(10) val upOffset: Long? = null,
        @ProtoNumber(11) val blockSize: Long? = null,
        @ProtoNumber(12) val newBigChan: Boolean? = null,
        @ProtoNumber(26) val upIp6: List<IPv6Info> = emptyList(),
        @ProtoNumber(27) val clientIp6: ByteArray? = null,
        @ProtoNumber(28) val downloadIndex: ByteArray? = null,
        @ProtoNumber(1001) val info4Busi: Busi? = null,
    )

    @Serializable
    data class Busi(
        @ProtoNumber(1) val downDomain: ByteArray? = null,
        @ProtoNumber(2) val thumbDownUrl: ByteArray? = null,
        @ProtoNumber(3) val originalDownUrl: ByteArray? = null,
        @ProtoNumber(4) val bigDownUrl: ByteArray? = null,
        @ProtoNumber(5) val fileResourceID: ByteArray? = null,
    )

    @Serializable
    data class IPv6Info(
        @ProtoNumber(1) val ip6: ByteArray? = null,
        @ProtoNumber(2) val port: Int? = null,
    )

    @Serializable
    data class ImageInfo(
        @ProtoNumber(1) val fileMd5: ByteArray? = null,
        @ProtoNumber(2) val fileType: Int? = null,
        @ProtoNumber(3) val fileSize: Long? = null,
        @ProtoNumber(4) val fileWidth: Int? = null,
        @ProtoNumber(5) val fileHeight: Int? = null,
    )

}