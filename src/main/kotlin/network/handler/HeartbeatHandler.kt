package katium.client.qq.network.handler

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.QQBot
import katium.client.qq.network.packet.heartbeat.HeartbeatAlivePacket
import katium.core.event.BotOfflineEvent
import katium.core.event.BotOnlineEvent
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HeartbeatHandler : EventListener {

    val body: ByteBuf = ByteBufAllocator.DEFAULT.directBuffer(0)

    @Subscribe
    fun onOnline(event: BotOnlineEvent) {
        val (bot) = event
        bot as QQBot
        if (!(bot.config["qq.heartbeat.enabled"] ?: "true").toBoolean())
            return
        bot.client.heartbeatJob = bot.launch {
            var times = 0
            while (currentCoroutineContext()[Job]!!.isActive) {
                delay(bot.config["qq.heartbeat.interval"]?.toLong() ?: 30000)
                bot.client.send(HeartbeatAlivePacket.create(bot.client))
                times++
                if (times >= 7) {
                    bot.client.registerClient()
                    times = 0
                }
            }
        }
    }

    @Subscribe
    fun onOffline(event: BotOfflineEvent) {
        val (bot) = event
        bot as QQBot
        bot.client.heartbeatJob?.cancel()
        bot.client.heartbeatJob = null
    }

}