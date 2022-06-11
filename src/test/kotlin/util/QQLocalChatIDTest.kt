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