package katium.client.qq.network.login.solution.provider

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket

object RetryLoginProvider : LoginSolutionProvider {

    override fun find(client: QQClient, response: LoginResponsePacket) = "Retry" to suspend { client.login() }

    override val id get() = "retry_login"
}