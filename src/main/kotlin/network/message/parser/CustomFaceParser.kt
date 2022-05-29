package katium.client.qq.network.message.parser

import katium.client.qq.message.content.QQImage
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.client.qq.network.pb.PbMessages

object CustomFaceParser : MessageParser {

    override suspend fun parse(
        client: QQClient,
        message: PbMessages.Message,
        element: PbMessageElements.Element
    ) = element.customFace.run {
        QQImage(
            resourceKey = fileID.toString(),
            originUrl = origUrl.substring(1), // remove `/` prefix
            md5 = md5,
            filePath = filePath,
            size = size,
            width = if (hasWidth()) width else null,
            height = if (hasHeight()) height else null
        )
    }

}