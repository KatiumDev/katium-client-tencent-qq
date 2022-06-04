package katium.client.qq.network.message.encoder

import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.core.message.content.MessageContent

object FallbackEncoder : MessageEncoder<MessageContent> {

    override suspend fun encode(
        client: QQClient,
        context: QQChat,
        message: MessageContent
    ) = arrayOf(
        PbMessageElements.Element.newBuilder()
            .setText(
                PbMessageElements.Text.newBuilder()
                    .setString(message.asString())
                    .build()
            )
            .build()
    )

}