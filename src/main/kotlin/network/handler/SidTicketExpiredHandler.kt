package katium.client.qq.network.handler

import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.client.qq.network.packet.onlinePush.SidTicketExpiredResponse
import katium.client.qq.network.packet.wtlogin.UpdateSigRequest
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe

object SidTicketExpiredHandler : EventListener {

    @Subscribe
    suspend fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is TransportPacket.Response.Buffered && packet.command == "OnlinePush.SidTicketExpired") {
            client.logger.info("Sig ticket expired, updating...")
            client.sendAndWait(UpdateSigRequest.create(client, mainSigMap = 3554528))
            client.registerClient()
            client.send(SidTicketExpiredResponse.create(client, sequenceID = packet.sequenceID))
            client.logger.info("Client resigned")
        }
    }

}
