package katium.client.qq.network.message.encoder

import com.google.protobuf.ByteString
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.core.group.Group
import katium.core.message.content.At
import katium.core.util.netty.toArray

object AtEncoder : MessageEncoder<At> {

    override suspend fun encode(client: QQClient, context: QQChat, message: At) = message.run {
        val targetUser = client.bot.getUser(target)
        if(context.context !is Group)
            throw UnsupportedOperationException("AT user in non-group chat")
        val text = "@${targetUser.name}" // @TODO: use group nick when At
        arrayOf(
            PbMessageElements.Element.newBuilder()
                .setText(
                    PbMessageElements.Text.newBuilder()
                        .setString(text)
                        .setAttribute6Buf(
                            ByteString.copyFrom(
                                ByteBufAllocator.DEFAULT.heapBuffer()
                                    .writeShort(1) // constant
                                    .writeShort(0) // start pos
                                    .writeShort(text.length)
                                    .writeByte(0) // flag
                                    .writeInt(targetUser.id.toInt()) // uin
                                    .writeShort(0) // const
                                    .toArray(release = true)
                            )
                        )
                )
                .build(),
            PbMessageElements.Element.newBuilder()
                .setText(PbMessageElements.Text.newBuilder().setString(" "))
                .build()
        )
    }

}