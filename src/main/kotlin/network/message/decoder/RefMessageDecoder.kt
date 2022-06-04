package katium.client.qq.network.message.decoder

import katium.client.qq.chat.QQChat
import katium.client.qq.group.QQGroup
import katium.client.qq.message.QQMessageRef
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.client.qq.network.pb.PbMessages
import katium.core.message.content.RefMessage

object RefMessageDecoder : MessageDecoder {

    override suspend fun decode(
        client: QQClient,
        context: QQChat,
        message: PbMessages.Message,
        element: PbMessageElements.Element
    ) = element.source.run {
        RefMessage(QQMessageRef(
            bot = client.bot,
            message = null,
            sequence = getOriginSequences(0),
            contextGroupCode = (context.contextGroup as? QQGroup)?.id
        ))
    }

}