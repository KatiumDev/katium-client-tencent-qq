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
package katium.client.qq.network.message.encoder

import com.google.common.hash.Hashing
import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQImage
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessageElement
import katium.core.message.content.Image
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.*

object ImageEncoder : MessageEncoder<Image> {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun encode(
        client: QQClient, context: QQChat, message: Image, withGeneralFlags: Boolean, isStandalone: Boolean
    ) = run {
        val resourceKey = if (message is QQImage) message.resourceKey
        else context.uploadImage(message.contentBytes!!).resourceKey!!
        @Suppress("DEPRECATION") val md5 = if (message is QQImage) message.md5
        else Hashing.md5().hashBytes(message.contentBytes!!).asBytes()
        if (context.contextContact != null) {
            arrayOf(
                PbMessageElement(
                    notOnlineImage = PbMessageElement.NotOnlineImage(
                        filePath = resourceKey,
                        resourceID = resourceKey,
                        oldPictureMd5 = false,
                        pictureMd5 = md5,
                        downloadPath = resourceKey,
                        original = 1,
                        pbReserve = byteArrayOf(0x78, 0x02),
                        thumbWidth = message.width,
                        thumbHeight = message.height
                    )
                )
            )
        } else {
            println(resourceKey)
            println(message)
            println(md5)
            println("${HexFormat.of().formatHex(md5).uppercase()}.jpg")
            println(PbMessageElement(
                customFace = PbMessageElement.CustomFace(
                    //filePath = "{${UUID.nameUUIDFromBytes(md5.toByteArray())}}.png".uppercase(),
                    filePath = if (message is QQImage) message.filePath
                    else "${HexFormat.of().formatHex(md5).uppercase()}.jpg",
                    fileID = resourceKey.toLong(),
                    fileType = 66,
                    useful = 1,
                    md5 = md5,
                    bizType = 5,
                    imageType = 1000,
                    width = message.width ?: 720,
                    height = message.height ?: 480,
                    thumbWidth = message.width,
                    thumbHeight = message.height,
                    source = 200,
                    size = if (message is QQImage) message.size!!
                    else message.contentBytes!!.size,
                    //origin = 0,
                    showLen = 0,
                    downloadLen = 0,
                    pbReserve = ProtoBuf.encodeToByteArray(PbMessageElement.ResvAttributes())
                )
            ))
            arrayOf(
                PbMessageElement(
                    customFace = PbMessageElement.CustomFace(
                        //filePath = "{${UUID.nameUUIDFromBytes(md5.toByteArray())}}.png".uppercase(),
                        filePath = if (message is QQImage) message.filePath
                        else "${HexFormat.of().formatHex(md5).uppercase()}.jpg",
                        fileID = resourceKey.toLong(),
                        fileType = 66,
                        useful = 1,
                        md5 = md5,
                        bizType = 5,
                        imageType = 1000,
                        width = message.width ?: 720,
                        height = message.height ?: 480,
                        thumbWidth = message.width,
                        thumbHeight = message.height,
                        source = 200,
                        size = if (message is QQImage) message.size!!
                        else message.contentBytes!!.size,
                        origin = 0,
                        showLen = 0,
                        downloadLen = 0,
                        pbReserve = ProtoBuf.encodeToByteArray(PbMessageElement.ResvAttributes())
                    )
                )
            )
        }
    }

}