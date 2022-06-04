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
import katium.client.qq.message.builder.QQJsonMessage
import katium.client.qq.message.builder.QQXmlMessage
import katium.client.qq.message.content.QQServiceMessage
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.client.qq.network.pb.PbMessages
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.util.zip.InflaterInputStream

object QQServiceMessageDecoder : MessageDecoder {

    override suspend fun decode(
        client: QQClient,
        context: QQChat,
        message: PbMessages.Message,
        element: PbMessageElements.Element
    ) = element.serviceMessage.run {
        val templateData = template.toByteArray()
        val contentData = if (templateData.isEmpty()) null else templateData.copyOfRange(1, templateData.size - 1)
        if (contentData == null) null else {
            val content = when (template.byteAt(0).toInt()) {
                0 -> String(contentData)
                1 -> String(InflaterInputStream(ByteArrayInputStream(contentData)).readAllBytes())
                else -> throw IllegalStateException("Unknown service message template type: ${template.byteAt(0)}")
            }
            when (serviceID) {
                33 -> null
                35 -> TODO("Decode forward messages https://cs.github.com/Mrs4s/MiraiGo/blob/master/message/message.go?q=RichMsg#L422")
                else -> if (content.contains("<?xml", ignoreCase = true)) {
                    QQXmlMessage(content, id = serviceID)
                } else {
                    try {
                        Json.Default.parseToJsonElement(content)
                        QQJsonMessage(content, id = serviceID)
                    } catch (e: SerializationException) {
                        QQServiceMessage(id = serviceID, type = QQServiceMessage.Type.UNKNOWN, content = content)
                    }
                }
            }
        }
    }

}