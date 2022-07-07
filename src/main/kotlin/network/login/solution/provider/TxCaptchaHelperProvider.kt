package katium.client.qq.network.login.solution.provider

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.core.util.okhttp.GlobalHttpClient
import katium.core.util.okhttp.await
import katium.core.util.okhttp.expected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.Request
import java.awt.Desktop
import java.net.URI

object TxCaptchaHelperProvider : LoginSolutionProvider {

    override val id: String get() = "tx_captcha_helper"

    override fun find(client: QQClient, response: LoginResponsePacket) =
        if (response.verifyUrl != null
            && response.errorMessage?.contains("captcha") != false
            && Desktop.isDesktopSupported()
        ) {
            "Resolve Captcha with TxCaptchaHelper" to suspend {
                val url = response.verifyUrl!!.replace("ssl.captcha.qq.com", "txhelper.glitch.me")
                client.logger.info("Resolving captcha with TxCaptchaHelper, url: $url")
                suspend fun fetch() = GlobalHttpClient.newCall(Request.Builder()
                        .get()
                        .url(url)
                        .build())
                        .await()
                        .expected(200)
                        .body.string()
                val base = fetch()
                client.logger.info(base)
                withTimeout(10 * 60 * 1000) {
                    while (true) {
                        delay(500)
                        val data = fetch()
                        if(data != base) {
                            client.logger.info("TxCaptchaHelper resolved detected")
                            client.submitCaptcha(data)
                            break
                        }
                    }
                }
            }
        } else null

}