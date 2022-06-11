package katium.client.qq.network.packet.chat

import com.google.common.hash.HashCode
import com.google.protobuf.ByteString
import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMultiMessages
import katium.core.util.netty.heapBuffer

object MultiMessagesUploadRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        buType: Int,
        groupUin: Long,
        size: Int,
        md5: HashCode,
    ) = TransportPacket.Request.Buffered(
        client = client,
        type = TransportPacket.Type.SIMPLE,
        encryptType = TransportPacket.EncryptType.D2_KEY,
        sequenceID = sequenceID,
        command = "MultiMsg.ApplyUp",
        body = PooledByteBufAllocator.DEFAULT.heapBuffer(
            createRequest(client, buType, groupUin, size, md5).toByteArray()
        )
    )

    fun createRequest(
        client: QQClient,
        buType: Int,
        groupUin: Long,
        size: Int,
        md5: HashCode,
    ): PbMultiMessages.MultiMessagesRequest =
        PbMultiMessages.MultiMessagesRequest.newBuilder().setSubCommand(1).setTermType(5).setPlatformType(9)
            .setNetworkType(3).setBuildVersion(client.version.version).setChannelType(0).setBuType(buType).addUpload(
                PbMultiMessages.MultiMessagesUploadRequest.newBuilder().setToUin(groupUin).setSize(size.toLong())
                    .setMd5(ByteString.copyFrom(md5.asBytes())).setType(3)
            ).build()

}