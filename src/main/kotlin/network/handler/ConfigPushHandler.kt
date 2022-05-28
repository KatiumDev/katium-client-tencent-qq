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

import katium.client.qq.network.codec.jce.SimpleJceStruct
import katium.client.qq.network.codec.jce.readJceStruct
import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.client.qq.network.packet.configPushSvc.ConfigPushRequest
import katium.client.qq.network.packet.configPushSvc.ConfigPushResponse
import katium.client.qq.network.sso.SsoServerListManager
import katium.client.qq.network.sso.SsoServerRecord
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import kotlinx.coroutines.launch

object ConfigPushHandler : EventListener {

    @Subscribe
    fun onPacket(event: QQPacketReceivedEvent) {
        val (_, client, packet) = event
        if (packet is ConfigPushRequest) {
            val action = packet.action
            if (action.buffer.isReadable)
                when (action.type) {
                    1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val servers = (action.buffer.duplicate().readJceStruct()[1u] as Collection<SimpleJceStruct>)
                            .map(::SsoServerRecord)
                        client.launch {
                            val addresses = SsoServerListManager.resolveRecords(servers)
                            client.serverAddresses += addresses
                            client.logger.info("Retrieved ${addresses.size} SSO server addresses from ConfigPush")
                        }
                    }
                    2 -> {
                        //@TODO: Decode highway config push https://cs.github.com/Mrs4s/MiraiGo/blob/master/client/decoders.go#L339
                    }
                    3 -> {} // log action
                    else -> throw UnsupportedOperationException("Unknown ConfigPush request type: ${action.type}")
                }
            client.send(
                ConfigPushResponse.create(
                    client,
                    sequenceID = packet.sequenceID,
                    type = action.type,
                    buffer = action.buffer.retainedDuplicate(),
                    actionSequenceID = action.sequenceID
                )
            )
        }
    }

}