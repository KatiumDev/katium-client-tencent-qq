package katium.client.qq.network.packet.longConn

import com.google.common.hash.HashCode
import com.google.protobuf.ByteString
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbCmd0x352
import katium.core.util.netty.heapBuffer

object QueryFriendImageRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        target: Long,
        md5: HashCode,
        fileSize: Int,
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "LongConn.OffPicUp",
            body = ByteBufAllocator.DEFAULT.heapBuffer(createRequest(client, target, md5, fileSize).toByteArray())
        )

    fun createRequest(
        client: QQClient,
        target: Long,
        md5: HashCode,
        fileSize: Int,
    ): PbCmd0x352.C352Request = PbCmd0x352.C352Request.newBuilder().apply {
        subCommand = 1
        addUploadRequest(PbCmd0x352.C352UploadRequest.newBuilder().apply {
            fromUin = client.uin
            toUin = target
            fileMd5 = ByteString.copyFrom(md5.asBytes())
            this.fileSize = fileSize.toLong()
            fileName = ByteString.copyFrom("$md5.jpg".toByteArray())
            sourceTerm = 5
            platformType = 9
            buType = 1
            pictureOriginal = true
            pictureType = 1000
            buildVersion = ByteString.copyFrom(client.clientVersion.version.toByteArray())
            fileIndex = ByteString.empty()
            srvUpload = 1
            transferUrl = ByteString.empty()
        })
    }.build()

}