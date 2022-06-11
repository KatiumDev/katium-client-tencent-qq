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
package katium.client.qq.test.util

import katium.client.qq.QQLocalChatID
import katium.client.qq.asQQ
import katium.core.chat.LocalChatID
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class QQLocalChatIDTest {

    @Test
    fun resolve() {
        assertEquals(123456, LocalChatID("123456").asQQ.uin)
        assertEquals(456789798461, LocalChatID("456789798461").asQQ.uin)
    }

    @Test
    fun `invalid long`() {
        assertThrows<NumberFormatException> { LocalChatID("123456abcd").asQQ.uin }
        assertThrows<NumberFormatException> { LocalChatID("456798416345s6f74sad").asQQ.uin }
        assertThrows<NumberFormatException> { LocalChatID("456789798461456789798461456789798461456789798461456789798461").asQQ.uin }
    }

    @Test
    fun `self resolve`() {
        QQLocalChatID(123456).also { assertEquals(it, it.asQQ) }
        QQLocalChatID(456789798461).also { assertEquals(it, it.asQQ) }
    }

}