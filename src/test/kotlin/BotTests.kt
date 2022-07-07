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
package katium.client.qq.test

import katium.client.qq.QQBotPlatform
import katium.core.event.BotOnlineEvent
import katium.core.util.event.Subscribe
import katium.core.util.event.register
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.File
import java.util.*
import kotlin.test.Test

object BotTests {

    @Suppress("UNCHECKED_CAST")
    val properties =
        File("local.properties").reader().use { Properties().apply { load(it) } }.toMap() as Map<String, String>

    val bot by lazy {
        runBlocking {
            withTimeout(30000) {
                println("bot creating")
                val bot = QQBotPlatform.createBot(properties)
                println("bot created")
                bot.register(object : EventListener {

                    @Subscribe
                    fun onMessage(event: katium.core.event.MessageReceivedEvent) {
                        println("Received message ${event.message}")
                    }

                })
                println("bot starting")
                bot.start()
                println("bot started")
                bot.launch {
                    delay(240000)
                    bot.stop()
                    throw IllegalStateException("Bot test timeout")
                }
                println("waiting for bot online")
                bot.eventBus.await(BotOnlineEvent::class)
                println("bot online, test starting")
                bot
            }
        }
    }

    val testFriend by lazy { bot.getUserSync(properties["test_friend"]!!.toLong()) }
    val testGroup by lazy { bot.getGroupSync(properties["test_group"]!!.toLong())!! }

    val testChats by lazy {
        arrayOf(testFriend.chat, testGroup.chat).filterNotNull().toTypedArray()
    }

    @Test
    fun printTestChats() {
        println("Current test chats: $testChats")
    }

}