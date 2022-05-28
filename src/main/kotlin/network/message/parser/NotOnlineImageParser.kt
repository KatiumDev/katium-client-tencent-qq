package katium.client.qq.network.message.parser

import katium.client.qq.message.content.QQImage
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.client.qq.network.pb.PbMessages

object NotOnlineImageParser : MessageParser {

    override suspend fun parse(
        client: QQClient,
        message: PbMessages.Message,
        element: PbMessageElements.Element
    ) = QQImage(element.notOnlineImage.resourceID, element.notOnlineImage.origUrl, element.notOnlineImage.pictureMd5)

}