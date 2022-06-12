package katium.client.qq.network.login.sms

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.core.util.service.Service
import katium.core.util.service.ServiceLookup

interface LoginSmsCodeProvider : Service {

    companion object {

        val lookup = ServiceLookup(LoginSmsCodeProvider::class)

    }

    suspend fun provide(client: QQClient, response: LoginResponsePacket): String?

}