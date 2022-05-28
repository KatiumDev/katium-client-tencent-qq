package katium.client.qq.network.handler

import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.client.qq.network.event.QQReceivedRawMessageEvent
import katium.client.qq.network.packet.onlinePush.PushGroupMessagesPacket
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import katium.core.util.event.post

object GroupMessagesHandler : EventListener {

    @Subscribe
    suspend fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is PushGroupMessagesPacket) {
            val response = packet.response
            val message = response.message
            client.synchronzier.recordUnreadGroupMessage(
                message.header.groupInfo.groupCode,
                message.header.sequence.toLong()
            )
            client.bot.post(QQReceivedRawMessageEvent(client, message))
        }
    }

}