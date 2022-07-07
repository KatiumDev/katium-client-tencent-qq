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

import katium.client.qq.QQLocalChatID
import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessage
import katium.client.qq.network.message.pb.PbMessageElement
import katium.core.message.builder.AtAll
import katium.core.message.content.At
import katium.core.message.content.PlainText
import java.io.ByteArrayInputStream
import java.io.DataInputStream

object PlainTextDecoder : MessageDecoder<PbMessageElement.Text> {

    override fun select(element: PbMessageElement) = element.text

    override suspend fun decode(
        client: QQClient, context: QQChat, message: PbMessage, element: PbMessageElement.Text
    ) = element.run {
        if (attribute6Buf == null || attribute6Buf.isEmpty()) {
            PlainText(string)
        } else {
            val uin = DataInputStream(ByteArrayInputStream(attribute6Buf)).use {
                it.skip(7)
                it.readInt().toLong()
            }
            if (uin != 0L) {
                At(QQLocalChatID(uin))
            } else {
                AtAll()
            }
        }
    }

}