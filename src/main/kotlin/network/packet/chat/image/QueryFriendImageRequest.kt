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
import katium.client.qq.network.pb.PbCmd0x352
import katium.core.util.netty.heapBuffer

object QueryFriendImageRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        target: Long,
        md5: HashCode,
        fileSize: Int,
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "LongConn.OffPicUp",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(createRequest(client, target, md5, fileSize).toByteArray())
        )

    fun createRequest(
        client: QQClient,
        target: Long,
        md5: HashCode,
        fileSize: Int,
    ): PbCmd0x352.C352Request = PbCmd0x352.C352Request.newBuilder().apply {
        subCommand = 1
        addUploadRequest(PbCmd0x352.C352UploadRequest.newBuilder().apply {
            fromUin = client.uin
            toUin = target
            fileMd5 = ByteString.copyFrom(md5.asBytes())
            this.fileSize = fileSize.toLong()
            fileName = ByteString.copyFrom("$md5.jpg".toByteArray())
            sourceTerm = 5
            platformType = 9
            buType = 1
            pictureOriginal = true
            pictureType = 1000
            buildVersion = ByteString.copyFrom(client.version.version.toByteArray())
            fileIndex = ByteString.empty()
            srvUpload = 1
            transferUrl = ByteString.empty()
        })
    }.build()

}