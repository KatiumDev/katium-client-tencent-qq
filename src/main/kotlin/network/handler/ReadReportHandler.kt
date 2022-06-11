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
import katium.core.event.BotOfflineEvent
import katium.core.event.BotOnlineEvent
import katium.core.util.event.Subscribe
import kotlinx.coroutines.*
import java.util.*
import kotlin.random.Random

object ReadReportHandler : EventListener {

    @Subscribe
    fun onOnline(event: BotOnlineEvent) {
        val (bot) = event
        bot as QQBot
        if (!bot.options.autoReadReportEnabled)
            return
        bot.client.synchronzier.readReportJob = bot.launch(CoroutineName("Read Report")) {
            while (!currentCoroutineContext()[Job]!!.isCancelled) {
                delay(
                    Random.Default.nextLong(
                        bot.options.autoReadReportIntervalMin,
                        bot.options.autoReadReportIntervalMax
                    )
                )
                if (bot.options.autoReadReportFull) {
                    bot.client.synchronzier.reportAllGroupRead()
                    bot.client.synchronzier.reportAllFriendRead()
                } else {
                    bot.client.synchronzier.reportSomeGroupRead()
                    bot.client.synchronzier.reportSomeFriendRead()
                }
            }
        }
    }

    @Subscribe
    fun onOffline(event: BotOfflineEvent) {
        val (bot) = event
        bot as QQBot
        bot.client.synchronzier.readReportJob?.cancel()
        bot.client.synchronzier.readReportJob = null
    }

}