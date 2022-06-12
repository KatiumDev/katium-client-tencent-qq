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
import katium.core.event.MessageReceivedEvent
import katium.core.util.event.Subscribe
import katium.core.util.event.register
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import java.io.File
import java.util.*
import kotlin.test.Test

object BotTests {

    @Suppress("UNCHECKED_CAST")
    val properties =
        File("local.properties").reader().use { Properties().apply { load(it) } }.toMap() as Map<String, String>

    val bot = QQBotPlatform.createBot(properties)

    init {
        bot.register(object : EventListener {

            @Subscribe
            fun onMessage(event: MessageReceivedEvent) {
                println("Received message ${event.message}")
            }

        })
        bot.start()
        bot.launch {
            delay(2400)
            bot.stop()
            throw IllegalStateException("Bot test timeout")
        }
        runBlocking {
            println("waiting for bot online")
            bot.eventBus.await(BotOnlineEvent::class)
            println("bot online, test starting")
        }
    }

    val testFriend = if("test_friend" in properties) bot.getUserSync(properties["test_friend"]!!.toLong()) else null
    val testGroup = if("test_group" in properties) bot.getGroupSync(properties["test_group"]!!.toLong()) else null

    init {
        if(testFriend != null) System.setProperty("ktmqq.has_test_friend", "true")
        if(testGroup != null) System.setProperty("ktmqq.has_test_group", "true")
    }

    val testChats = arrayOf(testFriend?.chat, testGroup?.chat).filterNotNull().toTypedArray()

    @Test
    @EnabledIfSystemProperty(named = "ktmqq.has_test_friend", matches = "true")
    fun hasTestFriend() {
        println("Current test friend: $testFriend")
    }

    @Test
    @EnabledIfSystemProperty(named = "ktmqq.has_test_group", matches = "true")
    fun hasTestGroup() {
        println("Current test group: $testGroup")
    }

    @Test
    fun printTestChats() {
        println("Current test chats: $testChats")
    }

}