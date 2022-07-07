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
package katium.client.qq.test.chat

import katium.client.qq.message.builder.QQForward
import katium.client.qq.message.builder.QQUrlShare
import katium.client.qq.test.BotTests
import katium.core.message.VirtualMessage
import katium.core.message.builder.*
import katium.core.message.content.PlainText
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class QQChatTest {

    @Test
    fun `send plain text`() {
        runBlocking {
            BotTests.testChats.forEach {
                it.sendMessage(PlainText("test plain text"))
            }
        }
    }

    @Test
    fun `send image`() {
        runBlocking {
            BotTests.testChats.forEach {
                it.sendMessage(Image("https://random.imagecdn.app/500/150"))
            }
        }
    }

    @Test
    fun `send reply`() {
        runBlocking {
            BotTests.testChats.forEach {
                val ref = it.sendMessage(PlainText("test reply target"))!!
                it.sendMessage(Reply(ref) + PlainText("test reply"))
                it.sendMessage(Ref(ref) + PlainText("test ref"))
            }
        }
    }

    @Test
    fun `send url share`() {
        runBlocking {
            BotTests.testChats.forEach {
                it.sendMessage(QQUrlShare("https://example.com/", "Example URL Share"))
            }
        }
    }

    @Test
    fun `send forward message`() {
        runBlocking {
            BotTests.testChats.forEach {
                val ref = it.sendMessage(PlainText("Actual message"))!!
                it.sendMessage(
                    Forward(
                        ref,
                        VirtualMessage(
                            BotTests.bot, it, sender = BotTests.testFriend, content = PlainText("Virtual message")
                        ).ref,
                        VirtualMessage(BotTests.bot, it, sender = BotTests.testFriend, content = Forward(ref)).ref
                    )
                )
                it.sendMessage(
                    QQForward(listOf(ref), title = "Custom forward title")
                )
            }
        }
    }

}