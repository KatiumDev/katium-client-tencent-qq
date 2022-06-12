package katium.client.qq.network.login.solution.selector

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.core.util.service.Service
import katium.core.util.service.ServiceLookup

interface LoginSolutionSelector : Service {

    companion object {

        val lookup = ServiceLookup(LoginSolutionSelector::class)

    }

    suspend fun select(client: QQClient, response: LoginResponsePacket, solutions: MutableMap<String, suspend () -> Unit>)

}