package katium.client.qq.network.login.solution.provider

import katium.client.qq.network.QQClient
import katium.client.qq.network.login.sms.LoginSmsCodeProvider
import katium.client.qq.network.packet.login.LoginResponsePacket

object SmsVerifyProvider : LoginSolutionProvider {

    override val id: String get() = "sms_verify"

    override fun find(client: QQClient, response: LoginResponsePacket) = if (response.smsPhone != null) {
        "SMS Verify(${response.smsPhone})" to suspend { client.requestSms() }
    } else null

}