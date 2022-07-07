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

import katium.client.qq.test.BotTests
import katium.core.message.builder.At
import katium.core.message.builder.AtAll
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class QQGroupTest {

    @Test
    fun `get name`() {
        runBlocking {
            BotTests.testGroup.run {
                println("Group name: $name")
            }
        }
    }

    @Test
    fun `send at`() {
        runBlocking {
            BotTests.testGroup.chat?.apply {
                sendMessage(At(BotTests.testFriend.localID))
            }
        }
    }

    @Test
    fun `send at all`() {
        runBlocking {
            BotTests.testGroup.chat?.apply {
                sendMessage(AtAll())
            }
        }
    }

}