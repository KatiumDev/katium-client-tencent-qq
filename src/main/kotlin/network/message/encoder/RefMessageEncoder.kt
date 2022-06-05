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
        client: QQClient, context: QQChat, message: RefMessage, isStandalone: Boolean
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
        ) + (if (message is QuoteReply) AtEncoder.encode(
            client, context, At(msg.sender), false
        ) else emptyArray()) + (if (isStandalone && message !is QuoteReply) PlainTextEncoder.encode(
            client, context, PlainText("^"), false
        ) else emptyArray())
    }

}