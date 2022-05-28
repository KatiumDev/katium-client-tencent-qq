package katium.client.qq.network.packet.messageSvc

import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMessagesReadReport
import katium.core.util.netty.heapBuffer

object MessageReadReportRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocSequenceID(),
        report: PbMessagesReadReport.ReadReportRequest
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "PbMessageSvc.PbMsgReadedReport",
            body = ByteBufAllocator.DEFAULT.heapBuffer(report.toByteArray())
        )

}
