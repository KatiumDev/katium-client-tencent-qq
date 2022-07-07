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
package katium.client.qq.network.message.decoder

import com.google.common.hash.HashCode
import katium.client.qq.chat.QQChat
import katium.client.qq.group.QQGroup
import katium.client.qq.message.content.QQImage
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessage
import katium.client.qq.network.message.pb.PbMessageElement

object CustomFaceDecoder : MessageDecoder<PbMessageElement.CustomFace> {

    override fun select(element: PbMessageElement) = element.customFace

    override suspend fun decode(
        client: QQClient, context: QQChat, message: PbMessage, element: PbMessageElement.CustomFace
    ) = element.run {
        val url = if (origUrl != null && origUrl.isNotEmpty()) {
            "https://${if (context.context is QQGroup) "gchat" else "c2cpicdw"}.qpic.cn$origUrl"
        } else {
            context.resolveImageUrl(HashCode.fromBytes(md5), size)
        }
        QQImage(
            resourceKey = fileID.toString(),
            contentUrl = url,
            md5 = md5,
            filePath = filePath,
            size = size,
            width = width,
            height = height,
        )
    }

}