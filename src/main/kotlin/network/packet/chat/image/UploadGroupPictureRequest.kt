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

import com.google.common.hash.HashCode
import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UploadGroupPictureRequest(
    @ProtoNumber(1) val networkType: Int? = null,
    @ProtoNumber(2) val subCommand: Int,
    @ProtoNumber(3) val uploadRequest: Set<C388UploadRequest> = emptySet(),
    /*@ProtoNumber(4) val getimgUrlReq: Set<GetImgUrlReq> = emptySet(),
    @ProtoNumber(5) val tryupPttReq: Set<TryUpPttReq> = emptySet(),
    @ProtoNumber(6) val getpttUrlReq: Set<GetPttUrlReq> = emptySet(),*/
    @ProtoNumber(7) val commandID: Int? = null,
    //@ProtoNumber(8) val delImgReq: Set<DelImgReq> = emptySet(),
    @ProtoNumber(1001) val extension: ByteArray? = null,
) {

    companion object {

        fun create(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            groupCode: Long,
            md5: HashCode,
            fileSize: Int,
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "ImgStore.GroupPicUp",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                ProtoBuf.encodeToByteArray(
                    createRequest(
                        client, groupCode, md5, fileSize
                    )
                )
            )
        )

        fun createRequest(
            client: QQClient,
            groupCode: Long,
            md5: HashCode,
            fileSize: Int,
        ) = UploadGroupPictureRequest(
            networkType = 3, // wifi
            subCommand = 1, uploadRequest = setOf(
                C388UploadRequest(
                    groupCode = groupCode,
                    fromUin = client.uin,
                    fileID = 0,
                    fileMD5 = md5.asBytes(),
                    fileSize = fileSize.toLong(),
                    fileName = "${md5.toString().uppercase()}.jpg".toByteArray(),
                    sourceTerm = 5,
                    platformType = 9,
                    buType = 1,
                    pictureWidth = 0,
                    pictureHeight = 0,
                    pictureType = 1000,
                    buildVersion = client.version.version.toByteArray(),
                    appPictureType = 1052,
                    originalPicture = 0
                )
            )
        )

    }

    @Serializable
    data class C388UploadRequest(
        @ProtoNumber(1) val groupCode: Long,
        @ProtoNumber(2) val fromUin: Long,
        @ProtoNumber(3) val fileID: Long,
        @ProtoNumber(4) val fileMD5: ByteArray,
        @ProtoNumber(5) val fileSize: Long,
        @ProtoNumber(6) val fileName: ByteArray,
        @ProtoNumber(7) val sourceTerm: Int,
        @ProtoNumber(8) val platformType: Int,
        @ProtoNumber(9) val buType: Int,
        @ProtoNumber(10) val pictureWidth: Int,
        @ProtoNumber(11) val pictureHeight: Int,
        @ProtoNumber(12) val pictureType: Int,
        @ProtoNumber(13) val buildVersion: ByteArray,
        @ProtoNumber(14) val innerIp: Int? = null,
        @ProtoNumber(15) val appPictureType: Int,
        @ProtoNumber(16) val originalPicture: Int,
        @ProtoNumber(17) val fileIndex: ByteArray? = null,
        @ProtoNumber(18) val dstUin: Long? = null,
        @ProtoNumber(19) val srvUpload: Int? = null,
        @ProtoNumber(20) val transferUrl: ByteArray? = null,
        @ProtoNumber(21) val qqMeetGuildId: Long? = null,
        @ProtoNumber(22) val qqMeetChannelId: Long? = null,
    )

}