package katium.client.qq.network.message.encoder

import com.google.common.hash.Hashing
import com.google.protobuf.ByteString
import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQImage
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.core.message.content.Image
import java.util.*

object ImageEncoder : MessageEncoder<Image> {

    override suspend fun encode(client: QQClient, context: QQChat, message: Image): Array<PbMessageElements.Element> {
        val resourceKey =
            if (message is QQImage) message.resourceKey
            else context.uploadImage(message.contentBytes!!).resourceKey!!
        val md5 =
            if (message is QQImage) message.md5
            else ByteString.copyFrom(Hashing.md5().hashBytes(message.contentBytes!!).asBytes())
        if (context.contextContact != null) {
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
                            .also {
                                if (message.width != null) it.thumbWidth = message.width!!
                                if (message.height != null) it.thumbHeight = message.height!!
                            }
                    )
                    .build()
            )
        } else {
            return arrayOf(
                PbMessageElements.Element.newBuilder()
                    .setCustomFace(
                        PbMessageElements.CustomFace.newBuilder()
                            //.setFilePath("{${UUID.nameUUIDFromBytes(md5.toByteArray())}}.png".uppercase())
                            .setFilePath(
                                if (message is QQImage) message.filePath
                                else "${HexFormat.of().formatHex(md5.toByteArray()).uppercase()}.jpg"
                            )
                            .setFileID(resourceKey.toLong())
                            //.setServerIP()
                            //.setServerPort()
                            .setFileType(66)
                            //.setSignature()
                            .setUseful(1)
                            .setMd5(md5)
                            .setBizType(5)
                            .setImageType(1000) // @TODO: 2000 for gif?
                            .setWidth(message.width ?: 720)
                            .setHeight(message.height ?: 480)
                            .also {
                                if (message.width != null) it.thumbWidth = message.width!!
                                if (message.height != null) it.thumbHeight = message.height!!
                            }
                            .setSource(200)
                            .setSize(
                                if (message is QQImage) message.size!!
                                else message.contentBytes!!.size
                            )
                            .setOrigin(0)
                            .setShowLen(0)
                            .setDownloadLen(0)
                            .setPbReserve(PbMessageElements.ResvAttributes.newBuilder().build().toByteString())
                    )
                    .build()
            )
        }
    }

}