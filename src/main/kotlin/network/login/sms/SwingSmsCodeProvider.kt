package katium.client.qq.network.login.sms

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket
import java.awt.Dimension
import javax.swing.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object SwingSmsCodeProvider : LoginSmsCodeProvider {

    override val id: String get() = "swing"

    override suspend fun provide(client: QQClient, response: LoginResponsePacket) =
        suspendCoroutine { continuation ->
            try {
                SwingUtilities.invokeAndWait {
                    JDialog().apply {
                        title = "Katium TX QQ - Input SMS Code"
                        size = Dimension(600, 150)
                        isVisible = true
                        rootPane.apply {
                            contentPane = Box.createVerticalBox()
                            contentPane.add(JLabel(response.errorMessage))
                            contentPane.add(JLabel("SMS code has already sent to ${response.smsPhone}"))
                            contentPane.add(JLabel("Please input the SMS code:"))
                            val input = JTextField().apply {
                                toolTipText = "SMS code"
                            }
                            contentPane.add(input)
                            contentPane.add(JButton("Submit").apply {
                                addActionListener {
                                    dispose()
                                    continuation.resume(input.text.trim().takeIf { it.isNotEmpty() })
                                }
                            })
                        }
                        invalidate()
                    }
                }
            } catch (e: ClassNotFoundException) {
                continuation.resume(null)
            }
        }

}