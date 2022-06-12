package katium.client.qq.network.login.sms

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket

object ConsoleSmsCodeProvider : LoginSmsCodeProvider {

    override val id: String get() = "console"

    override val priority: Int get() = 8

    override suspend fun provide(client: QQClient, response: LoginResponsePacket): String? {
        println("=====Katium TX QQ - Login SMS Code=====")
        println("SMS code has already sent to ${response.smsPhone}")
        println("Please input the received SMS code:")
        return (readLine() ?: throw UnsupportedOperationException("Standard input is not a valid tty")).trim()
            .takeIf { it.isNotEmpty() }
    }

}