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

import katium.client.qq.network.codec.highway.Highway
import katium.client.qq.network.codec.jce.SimpleJceStruct
import katium.client.qq.network.codec.jce.readJceStruct
import katium.client.qq.network.event.QQPacketReceivedEvent
import katium.client.qq.network.packet.meta.configPush.ConfigPushRequest
import katium.client.qq.network.packet.meta.configPush.ConfigPushResponse
import katium.client.qq.network.packet.meta.configPush.FileStorageConfigPushData
import katium.client.qq.network.pb.PbCmd0x6ff
import katium.client.qq.network.sso.SsoServerListManager
import katium.client.qq.network.sso.SsoServerRecord
import katium.core.util.event.Subscribe
import katium.core.util.netty.toArray
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.net.InetSocketAddress

object ConfigPushHandler : QQClientHandler {

    override val id: String get() = "config_push_handler"

    @OptIn(ExperimentalSerializationApi::class)
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
                        FileStorageConfigPushData(action.buffer.duplicate().readJceStruct()).use { list ->
                            val response = ProtoBuf.decodeFromByteArray<PbCmd0x6ff.Response>(
                                list.bigDataChannel.buffer.toArray(release = false)
                            )
                            client.highway.sessionSig = response.body.sigSession
                            client.highway.sessionKey = response.body.sessionKey
                            client.logger.info("Retrieved highway session key and sig from ConfigPush")
                            response.body.addresses.forEach {
                                val addresses = it.addresses.map { address ->
                                    InetSocketAddress(Highway.decodeIPv4(address.ip), address.port)
                                }
                                when (it.serviceType) {
                                    10 -> {
                                        client.logger.info("Retrieved ${addresses.size} highway SSO addresses from ConfigPush")
                                        client.highway.ssoAddresses.addAll(addresses)
                                    }
                                    21 -> {
                                        client.logger.info("Retrieved ${addresses.size} highway other addresses from ConfigPush")
                                        client.highway.otherAddresses.addAll(addresses)
                                    }
                                }
                            }
                        }
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