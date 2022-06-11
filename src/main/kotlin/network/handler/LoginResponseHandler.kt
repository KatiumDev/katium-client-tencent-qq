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
import katium.client.qq.network.packet.login.DeviceLockLoginPacket
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.core.util.event.Subscribe
import java.util.*

object LoginResponseHandler : EventListener {

    @Subscribe
    suspend fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is TransportPacket.Response.Oicq && packet.packet is LoginResponsePacket) {
            val response = packet.packet as LoginResponsePacket
            if (response.success) {
                client.registerClient()
                client.notifyOnline()
                client.pullSystemMessages()
                client.startSyncMessages()
                client.logger.info("Login succeeded")
            } else {
                if (response.deviceLock) {
                    client.logger.info("Trying to login with device lock")
                    client.send(DeviceLockLoginPacket.create(client))
                } else {
                    client.bot.stop()
                    throw IllegalStateException("Login failed, $response")
                }
            }
        }
    }

}