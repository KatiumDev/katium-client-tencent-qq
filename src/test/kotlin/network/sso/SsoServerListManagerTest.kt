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
package katium.client.qq.test.network.sso

import katium.client.qq.message.builder.QQUrlShare
import katium.client.qq.network.sso.SsoServerListManager
import katium.client.qq.test.BotTests
import katium.core.BotPlatform
import katium.core.chat.LocalChatID
import katium.core.event.BotOnlineEvent
import katium.core.event.MessageReceivedEvent
import katium.core.message.Message
import katium.core.message.asVirtual
import katium.core.message.builder.At
import katium.core.message.builder.Forward
import katium.core.message.builder.Image
import katium.core.message.builder.plus
import katium.core.message.content.PlainText
import katium.core.util.event.AsyncMode
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import katium.core.util.event.register
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class SsoServerListManagerTest {

    @Test
    fun `fetch sso server list`() {
        runBlocking {
            val bot = BotTests.bot
            bot.register(object : EventListener {

                @Subscribe
                suspend fun message(event: MessageReceivedEvent) {
                    println("Received ${event.message}")
                }

                @Subscribe(async = AsyncMode.ASYNC)
                suspend fun online(event: BotOnlineEvent) {
                    println("online")
                    launch {
                        delay(240000)
                        event.bot.stop()
                        throw IllegalStateException("Bot test timeout")
                    }
                    println(event.bot.allContacts)
                    println(event.bot.allGroups)
                    lateinit var msg: Message
                    event.bot.getGroup(LocalChatID("957401329"))!!.chat!!.apply {
                        +"Bot Test Begin"
                        kotlin.runCatching {
                            +Image("https://bing.biturl.top/?resolution=1366&format=image")
                            +Image("https://bing.biturl.top/?resolution=3840&format=image&index=1")
                        }.exceptionOrNull()?.printStackTrace()
                        run {
                            val ref = (+"Send a message now!")!!
                            msg = event.bot.eventBus.await(MessageReceivedEvent::class).message
                            println(msg)
                            delay(1000)
                            +(At(msg.sender) + "Good Job!" + msg)
                            //+(AtAll() + "Test")
                            +(PlainText("You sent: ") + msg.content + ref + Forward(msg.ref))
                        }
                        +"Bot Test End"
                    }
                    event.bot.getUser(LocalChatID("2162465168"))!!.chat!!.apply {
                        +"Bot Test Begin"
                        run {
                            +Image("https://bing.biturl.top/?resolution=1366&format=image")
                            +(PlainText("testmsg") + QQUrlShare(
                                "https://www.bilibili.com/video/BV1fX4y1w75P",
                                "梗体中文 · 在线构建——Wiki蜂人资源包,要素++,模块化,茶馆工作室",
                                "梗体中文 · 在线构建：自定义你的梗体中文！我们将我的世界中的字符串替换成了一些知名/不知名的梗。",
                                "https://s1.ax1x.com/2022/06/03/XUjv34.png"
                            ))
                        }
                        run {
                            val ref = (+"Test Target Message")!!
                            delay(1000)
                            -ref
                        }
                        +(PlainText("Bot Test End") + Forward(msg.ref, msg.asVirtual().ref))
                    }
                    event.bot.stop()
                }

            })
            bot.startAndJoin()
        }
        runBlocking {
            println("All server records: ${SsoServerListManager.fetchRecords()}")
        }
    }

    @Test
    fun `resolve addresses`() {
        runBlocking {
            println("Resolved server addresses: ${SsoServerListManager.fetchAddresses()}")
        }
    }

    @Test
    fun `sorted addresses`() {
        runBlocking {
            println("Sorted server addresses: ${SsoServerListManager.fetchSortedAddresses()}")
        }
    }

    @Test
    fun `addresses for connection`() {
        runBlocking {
            println("Addresses for connection: ${SsoServerListManager.fetchAddressesForConnection()}")
        }
    }

}
