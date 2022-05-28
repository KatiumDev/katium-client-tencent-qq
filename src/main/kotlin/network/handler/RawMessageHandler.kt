package katium.client.qq.network.handler

import katium.client.qq.network.event.QQReceivedRawMessageEvent
import katium.core.event.ReceivedMessageEvent
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import katium.core.util.event.post

object RawMessageHandler : EventListener {

    @Subscribe
    suspend fun onMessage(event: QQReceivedRawMessageEvent) {
        val (_, client, message) = event
        client.bot.post(
            ReceivedMessageEvent(
                (client.messageDecoders[message.header.type]
                    ?: throw UnsupportedOperationException("Unknown message type: ${message.header.type}"))
                    .decode(client, message)
            )
        )
    }

}