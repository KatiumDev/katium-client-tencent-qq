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
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.client.qq.network.pb.PbMessages
import katium.core.message.builder.AtAll
import katium.core.message.content.At
import katium.core.message.content.PlainText
import java.io.DataInputStream

object PlainTextDecoder : MessageDecoder {

    override suspend fun decode(client: QQClient, message: PbMessages.Message, element: PbMessageElements.Element) =
        element.text.run {
            if (!hasAttribute6Buf() || attribute6Buf.isEmpty) {
                PlainText(string)
            } else {
                val uin = DataInputStream(attribute6Buf.newInput()).use {
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