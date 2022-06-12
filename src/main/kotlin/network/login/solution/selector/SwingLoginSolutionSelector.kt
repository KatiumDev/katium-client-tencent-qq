package katium.client.qq.network.login.solution.selector

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

object SwingLoginSolutionSelector : LoginSolutionSelector {

    override val id: String get() = "swing"

    override suspend fun select(
        client: QQClient, response: LoginResponsePacket, solutions: MutableMap<String, suspend () -> Unit>
    ) {
        withContext(Dispatchers.IO) {
            SwingUtilities.invokeAndWait {
                JDialog().apply {
                    title = "Katium TX QQ - Select Login Solution"
                    size = Dimension(600, 300)
                    isVisible = true
                    rootPane.apply {
                        contentPane = JPanel()
                        contentPane.layout = BorderLayout()
                        contentPane.add(JLabel(response.errorMessage), BorderLayout.NORTH)
                        contentPane.add(JList(solutions.keys.toTypedArray()).apply {
                            layoutOrientation = JList.VERTICAL_WRAP
                            selectionMode = ListSelectionModel.SINGLE_SELECTION
                            selectionModel.addListSelectionListener {
                                if (it.valueIsAdjusting) return@addListSelectionListener
                                dispose()
                                client.launch(CoroutineName("Solve Login with Swing")) {
                                    solutions[selectedValue]!!()
                                }
                            }
                        }, BorderLayout.CENTER)
                    }
                    invalidate()
                }
            }
        }
    }

}