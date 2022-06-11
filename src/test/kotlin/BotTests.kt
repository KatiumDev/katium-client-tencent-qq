package katium.client.qq.test

import katium.client.qq.QQBotPlatform
import java.io.File
import java.util.*

object BotTests {

    @Suppress("UNCHECKED_CAST")
    val properties =
        File("local.properties").reader().use { Properties().apply { load(it) } }.toMap() as Map<String, String>

    val bot = QQBotPlatform.createBot(properties)

}