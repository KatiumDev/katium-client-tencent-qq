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

import com.google.protobuf.ByteString
import katium.client.qq.asQQ
import katium.client.qq.chat.QQChat
import katium.client.qq.message.QQMessageRef
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.core.message.builder.At
import katium.core.message.content.MessageChain
import katium.core.message.content.PlainText
import katium.core.message.content.QuoteReply
import katium.core.message.content.RefMessage

object RefMessageEncoder : MessageEncoder<RefMessage> {

    override val priority get() = 15

    override val maxCountOneMessage get() = 1

    override suspend fun encode(
        client: QQClient, context: QQChat, message: RefMessage, withGeneralFlags: Boolean, isStandalone: Boolean
    ): Array<PbMessageElements.Element> {
        val ref = message.ref as QQMessageRef
        val msg = ref.message!!
        val content =
            msg.content.select { client.messageEncoders.shouldStandalone(it) }.let { (standaloneParts, mainParts) ->
                if (mainParts.isNotEmpty()) mainParts else arrayOf(PlainText(standaloneParts.joinToString(separator = "") { it.asString() }))
            }
        return arrayOf(
            PbMessageElements.Element.newBuilder().setSource(
                PbMessageElements.SourceMessage.newBuilder().addOriginSequences(ref.sequence)
                    .setSenderUin(msg.senderUser!!.localID.asQQ.uin).setTime((msg.time / 1000).toInt()).setFlag(1)
                    .addAllElements(client.messageEncoders.encode(context, MessageChain(*content)))
                    .setRichMessage(ByteString.empty()).setPbReserve(ByteString.empty())
                    .setSourceMessage(ByteString.empty()).setTroopName(ByteString.empty())
            ).build()
        ) + (if (message is QuoteReply) client.messageEncoders.encode(context, At(msg.sender))
            .toTypedArray() else emptyArray()) + (if (isStandalone && message !is QuoteReply) client.messageEncoders.encode(
            context, PlainText("^")
        ).toTypedArray() else emptyArray())
    }

}