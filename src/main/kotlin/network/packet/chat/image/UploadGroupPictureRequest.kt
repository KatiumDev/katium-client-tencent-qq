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
import com.google.protobuf.ByteString
import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbCmd0x388
import katium.core.util.netty.heapBuffer

object UploadGroupPictureRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        groupCode: Long,
        md5: HashCode,
        fileSize: Int,
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "ImgStore.GroupPicUp",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(createRequest(client, groupCode, md5, fileSize).toByteArray())
        )

    fun createRequest(
        client: QQClient,
        groupCode: Long,
        md5: HashCode,
        fileSize: Int,
    ): PbCmd0x388.C388Request = PbCmd0x388.C388Request.newBuilder().apply {
        networkType = 3 // wifi
        subCommand = 1
        addUploadRequest(PbCmd0x388.C388UploadRequest.newBuilder().apply {
            this.groupCode = groupCode
            fromUin = client.uin
            fileID = 0
            fileMd5 = ByteString.copyFrom(md5.asBytes())
            this.fileSize = fileSize.toLong()
            fileName = ByteString.copyFrom("${md5.toString().uppercase()}.jpg".toByteArray())
            sourceTerm = 5
            platformType = 9
            buType = 1
            pictureWidth = 0
            pictureHeight = 0
            pictureType = 1000
            buildVersion = ByteString.copyFrom(client.version.version.toByteArray())
            appPictureType = 1052
            originalPicture = 0
        })
    }.build()

}