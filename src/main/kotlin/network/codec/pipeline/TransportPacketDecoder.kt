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
package katium.client.qq.network.codec.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.event.QQTransportDecodersInitializeEvent
import katium.client.qq.network.packet.configPushSvc.ConfigPushRequest
import katium.client.qq.network.packet.messageSvc.PullMessagesResponse
import katium.client.qq.network.packet.messageSvc.PushNotifyPacket
import katium.client.qq.network.packet.onlinePush.PushGroupMessagesPacket
import katium.client.qq.network.packet.profileSvc.PullGroupSystemMessagesResponse
import katium.client.qq.network.packet.wtlogin.UpdateSigResponse
import katium.core.util.event.post
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking

class TransportPacketDecoder(val client: QQClient) : MessageToMessageDecoder<TransportPacket.Response>() {

    val decoders: Map<String, (QQClient, TransportPacket.Response.Buffered) -> TransportPacket.Response> by lazy {
        val decoders = mutableMapOf<String, (QQClient, TransportPacket.Response.Buffered) -> TransportPacket.Response>()
        registerBuiltinDecoders(decoders)
        runBlocking(CoroutineName("Initialize Transport Packet Decoders")) {
            client.bot.post(QQTransportDecodersInitializeEvent(client, decoders))
        }
        decoders.toMap()
    }

    private fun registerBuiltinDecoders(decoders: MutableMap<String, (QQClient, TransportPacket.Response.Buffered) -> TransportPacket.Response>) {
        decoders["ProfileService.Pb.ReqSystemMsgNew.Group"] = ::PullGroupSystemMessagesResponse
        decoders["ConfigPushSvc.PushReq"] = ::ConfigPushRequest
        decoders["MessageSvc.PushNotify"] = ::PushNotifyPacket
        decoders["MessageSvc.PbGetMsg"] = ::PullMessagesResponse
        decoders["OnlinePush.PbPushGroupMsg"] = ::PushGroupMessagesPacket
    }

    override fun decode(ctx: ChannelHandlerContext, msg: TransportPacket.Response, out: MutableList<Any>) {
        out.add(
            if (msg is TransportPacket.Response.Buffered) {
                decoders[msg.command]?.invoke(client, msg) ?: msg
            } else msg
        )
    }

}