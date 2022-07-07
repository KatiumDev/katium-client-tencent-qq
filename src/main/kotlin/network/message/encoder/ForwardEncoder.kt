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

import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQForward
import katium.client.qq.message.content.QQService
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessageElement
import katium.core.message.content.Forward
import org.redundent.kotlin.xml.XmlVersion
import org.redundent.kotlin.xml.xml

object ForwardEncoder : MessageEncoder<Forward> {

    override val shouldStandalone: Boolean get() = true

    override suspend fun encode(
        client: QQClient, context: QQChat, message: Forward, withGeneralFlags: Boolean, isStandalone: Boolean
    ): Array<PbMessageElement> = run {
        val qqForward = message as? QQForward
        val messages = message.messages.map { it.message!! }
        if (messages.size > client.bot.options.maxForwardMessageLength) throw UnsupportedOperationException("Forward message is too long, current: ${messages.size}, maxLength: ${client.bot.options.maxForwardMessageLength}")

        val resourceID = context.uploadMultiMessages(messages)
        val template = xml("msg", encoding = "utf-8", version = XmlVersion.V10) {
            attribute("serviceID", 35)
            attribute("templateID", 1)
            attribute("action", "viewMultiMsg")
            attribute("brief", qqForward?.brief ?: "[聊天记录]")
            attribute("m_resid", resourceID)
            attribute("m_fileName", System.currentTimeMillis())
            attribute("tSum", 3)
            attribute("sourceMsgId", 0)
            attribute("url", "")
            attribute("flag", 3)
            attribute("adverSign", 0)
            attribute("multiMsgFlag", 0)
            "item"("layout" to 1, "advertiser_id" to 0, "aid" to 0) {
                "title"("size" to 34, "maxLines" to 2, "lineSpace" to 12) {
                    -(qqForward?.title ?: "群聊的聊天记录")
                }
                if (qqForward?.preview == null) {
                    messages.take(if (messages.size > 4) 3 else 4).forEach {
                        "title"("size" to 26, "color" to "#777777", "maxLines" to 2, "lineSpace" to 12) {
                            -it.sender.name
                            -": "
                            -it.content.asString().take(50)
                        }
                    }
                    if (messages.size > 4) {
                        "title"("size" to 26, "color" to "#777777", "maxLines" to 2, "lineSpace" to 12) {
                            -"..."
                        }
                    }
                } else {
                    "title"("size" to 26, "color" to "#777777", "maxLines" to 2, "lineSpace" to 12) {
                        -qqForward.preview
                    }
                }
                "hr"("hidden" to false, "style" to 0)
                "summary"("size" to 26, "color" to "#777777") {
                    -(qqForward?.summary ?: "查看 ${messages.size} 条转发消息")
                }
            }
            "source"("name" to (qqForward?.source ?: "聊天记录"), "icon" to "", "action" to "", "appid" to -1)
        }.toString(prettyFormat = false)
        client.messageEncoders.encode(
            context, QQService(35, QQService.Type.LONG_MESSAGE, template, resourceID)
        ).toTypedArray()
    }

}