package katium.client.qq.network.message.decoder

import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.client.qq.network.pb.PbMessages

object FallbackDecoder : MessageDecoder {

    override suspend fun decode(
        client: QQClient, context: QQChat, message: PbMessages.Message, element: PbMessageElements.Element
    ) = null

}