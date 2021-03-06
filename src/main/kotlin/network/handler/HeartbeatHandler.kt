/*
 * Copyright 2022 Katium Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package katium.client.qq.network.handler

import katium.client.qq.QQBot
import katium.client.qq.network.packet.meta.HeartbeatAlivePacket
import katium.core.event.BotOfflineEvent
import katium.core.event.BotOnlineEvent
import katium.core.util.event.Subscribe
import kotlinx.coroutines.*
import java.util.*

object HeartbeatHandler : QQClientHandler {

    override val id: String get() = "heartbeat_handler"

    @Subscribe
    fun onOnline(event: BotOnlineEvent) {
        val (bot) = event
        bot as QQBot
        if (!bot.options.heartbeatEnabled)
            return
        bot.client.heartbeatJob = bot.launch(CoroutineName("Heartbeat")) {
            var times = 0
            while (!currentCoroutineContext()[Job]!!.isCancelled) {
                delay(bot.options.heartbeatInterval)
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