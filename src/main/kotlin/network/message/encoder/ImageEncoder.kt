package katium.client.qq.network.message.encoder

import com.google.common.hash.Hashing
import com.google.protobuf.ByteString
import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQImage
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.core.message.content.Image

object ImageEncoder : MessageEncoder<Image> {

    override suspend fun encode(client: QQClient, context: QQChat, message: Image): Array<PbMessageElements.Element> {
        val resourceKey =
            if (message is QQImage) message.resourceKey
            else context.uploadImage(message.contentBytes!!).resourceKey!!
        val md5 =
            if (message is QQImage) message.md5
            else ByteString.copyFrom(Hashing.md5().hashBytes(message.contentBytes!!).asBytes())
        @Suppress("DEPRECATION")
        return arrayOf(
            PbMessageElements.Element.newBuilder()
                .setNotOnlineImage(
                    PbMessageElements.NotOnlineImage.newBuilder()
                        .setFilePath(resourceKey)
                        .setResourceID(resourceKey)
                        .setOldPictureMd5(false)
                        .setPictureMd5(md5)
                        .setDownloadPath(resourceKey)
                        .setOriginal(1)
                        .setPbReserve(ByteString.copyFrom(byteArrayOf(0x78, 0x02)))
                )
                .build()
        )
    }

}