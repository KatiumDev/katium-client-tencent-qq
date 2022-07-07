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
data class QueryFriendImageRequest(
    @ProtoNumber(1) val subCommand: Int,
    @ProtoNumber(2) val uploadRequest: Set<C352UploadRequest> = emptySet(),
    //@ProtoNumber(3) val getimgUrlReq: Set<GetImgUrlReq> = emptySet(),
    //@ProtoNumber(4) val delImgReq: Set<DelImgReq> = emptySet(),
    @ProtoNumber(10) val networkType: Int? = null,
) {

    companion object {

        fun create(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            target: Long,
            md5: HashCode,
            fileSize: Int,
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "LongConn.OffPicUp",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                ProtoBuf.encodeToByteArray(
                    createRequest(
                        client, target, md5, fileSize
                    )
                )
            )
        )

        fun createRequest(
            client: QQClient,
            target: Long,
            md5: HashCode,
            fileSize: Int,
        ) = QueryFriendImageRequest(
            subCommand = 1, networkType = 3, // wifi
            uploadRequest = setOf(
                C352UploadRequest(
                    fromUin = client.uin,
                    toUin = target,
                    fileMD5 = md5.asBytes(),
                    fileSize = fileSize.toLong(),
                    fileName = "$md5.jpg".toByteArray(),
                    sourceTerm = 5,
                    platformType = 9,
                    buType = 1,
                    pictureOriginal = true,
                    pictureType = 1000,
                    buildVersion = client.version.version.toByteArray(),
                    fileIndex = ByteArray(0),
                    srvUpload = 1,
                    transferUrl = ByteArray(0)
                )
            )
        )

    }

    @Serializable
    data class C352UploadRequest(
        @ProtoNumber(1) val fromUin: Long? = null,
        @ProtoNumber(2) val toUin: Long? = null,
        @ProtoNumber(3) val fileID: Long? = null,
        @ProtoNumber(4) val fileMD5: ByteArray? = null,
        @ProtoNumber(5) val fileSize: Long? = null,
        @ProtoNumber(6) val fileName: ByteArray? = null,
        @ProtoNumber(7) val sourceTerm: Int? = null,
        @ProtoNumber(8) val platformType: Int? = null,
        @ProtoNumber(9) val innerIp: Int? = null,
        @ProtoNumber(10) val addressBook: Boolean? = null,
        @ProtoNumber(11) val retry: Int? = null,
        @ProtoNumber(12) val buType: Int? = null,
        @ProtoNumber(13) val pictureOriginal: Boolean? = null,
        @ProtoNumber(14) val pictureWidth: Int? = null,
        @ProtoNumber(15) val pictureHeight: Int? = null,
        @ProtoNumber(16) val pictureType: Int? = null,
        @ProtoNumber(17) val buildVersion: ByteArray? = null,
        @ProtoNumber(18) val fileIndex: ByteArray? = null,
        @ProtoNumber(19) val storeDays: Int? = null,
        @ProtoNumber(20) val stepFlag: Int? = null,
        @ProtoNumber(21) val rejectTryFast: Boolean? = null,
        @ProtoNumber(22) val srvUpload: Int? = null,
        @ProtoNumber(23) val transferUrl: ByteArray? = null,
    )

}