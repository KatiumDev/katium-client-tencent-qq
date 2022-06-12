package katium.client.qq.network.login.solution.selector

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.login.LoginResponsePacket

object ConsoleLoginSolutionSelector : LoginSolutionSelector {

    override val id: String get() = "console"

    override val priority: Int get() = 8

    override suspend fun select(
        client: QQClient, response: LoginResponsePacket, solutions: MutableMap<String, suspend () -> Unit>
    ) {
        println("=====Katium TX QQ - Select Login Solution=====")
        solutions.entries.forEachIndexed { index, (name, _) ->
            println("$index: $name")
        }
        println("Please select a login solution:")
        (readLine() ?: throw UnsupportedOperationException("Standard input is not a valid tty"))
            .toInt().let {
                solutions.values.elementAtOrNull(it)?.let { it() } ?: suspend {
                    println("Invalid number: $it")
                    select(client, response, solutions)
                }()
            }
    }

}