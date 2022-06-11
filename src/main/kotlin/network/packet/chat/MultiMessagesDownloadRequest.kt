package katium.client.qq.network.packet.chat

import com.google.protobuf.ByteString
import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMultiMessages
import katium.core.util.netty.heapBuffer

object MultiMessagesDownloadRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        buType: Int,
        resourceID: String,
    ) = TransportPacket.Request.Buffered(
        client = client,
        type = TransportPacket.Type.SIMPLE,
        encryptType = TransportPacket.EncryptType.D2_KEY,
        sequenceID = sequenceID,
        command = "MultiMsg.ApplyDown",
        body = PooledByteBufAllocator.DEFAULT.heapBuffer(
            createRequest(client, buType, resourceID).toByteArray()
        )
    )

    fun createRequest(
        client: QQClient,
        buType: Int,
        resourceID: String,
    ): PbMultiMessages.MultiMessagesRequest =
        PbMultiMessages.MultiMessagesRequest.newBuilder().setSubCommand(2).setTermType(5).setPlatformType(9)
            .setNetworkType(3).setBuildVersion(client.version.version).setBuType(buType).setChannelType(2)
            .addDownload(PbMultiMessages.MultiMessagesDownloadRequest.newBuilder()
                .setResourceID(ByteString.copyFrom(resourceID.toByteArray())).setType(3).setFromUin(client.uin).apply {
                    if (client.highway.sessionKey != null) key = ByteString.copyFrom(client.highway.sessionKey)
                    if (client.highway.sessionSig != null) sig = ByteString.copyFrom(client.highway.sessionSig)
                }).build()

}