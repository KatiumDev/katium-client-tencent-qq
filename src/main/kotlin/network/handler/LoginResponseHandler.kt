/*
 * Copyright 2022 Katium Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package katium.client.qq.network.handler

import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.client.qq.network.login.sms.LoginSmsCodeProvider
import katium.client.qq.network.login.solution.provider.LoginSolutionProvider
import katium.client.qq.network.login.solution.selector.LoginSolutionSelector
import katium.client.qq.network.packet.login.DeviceLockLoginPacket
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.core.util.event.AsyncMode
import katium.core.util.event.Subscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object LoginResponseHandler : QQClientHandler {

    override val id: String get() = "login_response_handler"

    @Subscribe(async = AsyncMode.ASYNC)
    suspend fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is TransportPacket.Response.Oicq && packet.packet is LoginResponsePacket) {
            val response = packet.packet as LoginResponsePacket
            client.logger.info("Got login response: $response")
            if (response.success) { // Succeeded
                client.loginSucceeded()
                return
            } else if (response.deviceLock) { // Device lock
                client.logger.info("Trying to login with device lock")
                client.send(DeviceLockLoginPacket.create(client))
                return
            } else if (response.smsSent) {
                client.logger.info("SMS sent, resolving with SMS provider...")
                val code = if (client.bot.options.loginSmsProvider != null) {
                    LoginSmsCodeProvider.lookup.resolve(client.bot.options.loginSmsProvider).provide(client, response)
                        ?: throw IllegalStateException("SMS provider ${client.bot.options.loginSmsProvider} could not provide any SMS code, $response")
                } else {
                    run {
                        val exception = RuntimeException("Unable to find SMS code for login")
                        LoginSmsCodeProvider.lookup.services.forEach {
                            try {
                                val code = it.provide(client, response)
                                if (code != null) return@run code
                            } catch (e: Throwable) {
                                exception.addSuppressed(e)
                            }
                        }
                        throw exception
                    }
                }
                client.submitSms(code)
                return
            } else if (client.bot.options.loginSolutionProvider != null) { // Resolve login solution
                val (solutionName, solution) = LoginSolutionProvider.lookup.resolve(client.bot.options.loginSolutionProvider)
                    .find(client, response)
                    ?: throw IllegalStateException("No solution found by ${client.bot.options.loginSolutionProvider}")
                client.logger.info("Solving login with \"$solutionName\"")
                solution()
                return
            } else if (client.bot.options.interactiveLogin) { // Interactive login solution selecting
                val solutions = mutableMapOf<String, suspend () -> Unit>()
                LoginSolutionProvider.lookup.services.mapNotNull { it.find(client, response) }
                    .forEach { solutions += it }
                if (solutions.isEmpty()) {
                    client.logger.warn("No solution found")
                } else if (client.bot.options.loginSolutionSelector != null) {
                    client.launch {
                        withContext(Dispatchers.IO) {
                            LoginSolutionSelector.lookup.resolve(client.bot.options.loginSolutionSelector)
                                .select(client, response, solutions)
                        }
                    }
                    return
                } else {
                    val exception = RuntimeException("Unable to select login solution")
                    LoginSolutionSelector.lookup.services.forEach {
                        try {
                            client.launch {
                                withContext(Dispatchers.IO) {
                                    it.select(client, response, solutions)
                                }
                            }
                            return
                        } catch (e: Throwable) {
                            exception.addSuppressed(e)
                        }
                    }
                    throw exception
                }
            }
            client.bot.stop()
            throw IllegalStateException("Login failed, $response")
        }
    }

}