package katium.client.qq.network.message.encoder

import com.google.protobuf.ByteString
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import katium.core.message.content.AtAll
import katium.core.util.netty.toArray

object AtAllEncoder: MessageEncoder<AtAll> {

    const val DISPLAY = "@全体成员"

    override suspend fun encode(client: QQClient, context: QQChat, message: AtAll) = message.run {
        arrayOf(
            PbMessageElements.Element.newBuilder()
                .setText(
                    PbMessageElements.Text.newBuilder()
                        .setString(DISPLAY)
                        .setAttribute6Buf(
                            ByteString.copyFrom(
                                ByteBufAllocator.DEFAULT.heapBuffer()
                                    .writeShort(1) // constant
                                    .writeShort(0) // start pos
                                    .writeShort(DISPLAY.length)
                                    .writeByte(1) // flag
                                    .writeInt(0) // uin
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