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

import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQImage
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessage
import katium.client.qq.network.message.pb.PbMessageElement

object NotOnlineImageDecoder : MessageDecoder<PbMessageElement.NotOnlineImage> {

    override fun select(element: PbMessageElement) = element.notOnlineImage

    override suspend fun decode(
        client: QQClient, context: QQChat, message: PbMessage, element: PbMessageElement.NotOnlineImage
    ) = element.run {
        QQImage(
            resourceKey = resourceID,
            contentUrl = "https://c2cpicdw.qpic.cn/$origUrl",
            md5 = pictureMd5,
            width = pictureWidth,
            height = pictureHeight
        )
    }

}