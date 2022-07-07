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
import katium.client.qq.message.QQMessage
import katium.client.qq.message.builder.QQForward
import katium.client.qq.message.builder.QQJson
import katium.client.qq.message.builder.QQXml
import katium.client.qq.message.content.QQService
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessage
import katium.client.qq.network.message.pb.PbMessageElement
import katium.client.qq.network.packet.chat.MultiMessagesDownloadRequest
import katium.client.qq.network.packet.chat.MultiMessagesDownloadResponse
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.redundent.kotlin.xml.TextElement
import org.redundent.kotlin.xml.parse
import java.io.ByteArrayInputStream
import java.util.zip.InflaterInputStream

object QQServiceDecoder : MessageDecoder<PbMessageElement.ServiceMessage> {

    override fun select(element: PbMessageElement) = element.serviceMessage

    override suspend fun decode(
        client: QQClient, context: QQChat, message: PbMessage, element: PbMessageElement.ServiceMessage
    ) = element.run {
        val templateData = template
        val contentData = if (templateData.isEmpty()) null else templateData.copyOfRange(1, templateData.size)
        if (contentData == null) null else {
            val content = when (template[0].toInt()) {
                0 -> String(contentData)
                1 -> String(InflaterInputStream(ByteArrayInputStream(contentData)).readAllBytes())
                else -> throw IllegalStateException("Unknown service message template type: ${template[0]}")
            }
            when (serviceID) {
                33 -> null
                35 -> parse(ByteArrayInputStream(content.toByteArray())).run {
                    val resourceID = attributes["m_resid"].toString()
                    //val fileName = attributes["m_fileName"].toString().takeIf { it.isNotEmpty() }
                    val item = filter("item").first()
                    lateinit var title: String
                    val preview = StringBuilder()
                    item.filter("title").forEach {
                        val titleContent = it.children.filterIsInstance<TextElement>()
                            .joinToString(separator = "", transform = TextElement::text)
                        when (it.attributes["size"].toString().toInt()) {
                            34 -> title = titleContent
                            26 -> preview.append(titleContent)
                        }
                    }
                    val downloadResponse = client.sendAndWait(
                        MultiMessagesDownloadRequest.create(
                            client, buType = 2, resourceID = resourceID
                        )
                    ) as MultiMessagesDownloadResponse
                    QQForward(
                        messages = downloadResponse.messages.get().map(QQMessage::ref),
                        title = title,
                        brief = attributes["brief"].toString(),
                        preview = preview.toString(),
                        summary = item.filter("summary").first().children.filterIsInstance<TextElement>()
                            .joinToString(separator = "", transform = TextElement::text),
                        source = filter("source").first().attributes["name"].toString()
                    )
                }
                else -> if (content.contains("<?xml", ignoreCase = true)) {
                    QQXml(content, id = serviceID)
                } else {
                    try {
                        Json.Default.parseToJsonElement(content)
                        QQJson(content, id = serviceID)
                    } catch (e: SerializationException) {
                        QQService(id = serviceID, type = QQService.Type.UNKNOWN, content = content)
                    }
                }
            }
        }
    }

}