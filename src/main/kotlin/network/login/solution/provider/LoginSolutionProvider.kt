package katium.client.qq.network.login.solution.provider

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.core.util.service.Service
import katium.core.util.service.ServiceLookup

interface LoginSolutionProvider : Service {

    companion object {

        val lookup = ServiceLookup(LoginSolutionProvider::class)

    }

    fun find(client: QQClient, response: LoginResponsePacket): Pair<String, suspend () -> Unit>?

}