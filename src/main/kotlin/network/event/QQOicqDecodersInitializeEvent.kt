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
package katium.client.qq.network.event

import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.oicq.OicqPacketCodec
import katium.core.event.BotEvent

class QQOicqDecodersInitializeEvent(
    val codec: OicqPacketCodec,
    val decoders: MutableMap<String, (QQClient, Int, Short) -> OicqPacket.Response.Simple>
) : BotEvent(codec.client.bot) {

    val client by codec::client

    operator fun component2() = codec
    operator fun component3() = decoders

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQOicqDecodersInitializeEvent) return false
        if (!super.equals(other)) return false
        if (codec != other.codec) return false
        if (decoders != other.decoders) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + codec.hashCode()
        result = 31 * result + decoders.hashCode()
        return result
    }

    override fun toString() = "QQOicqDecodersInitializeEvent(bot=$bot, codec=$codec, decoders=$decoders)"

}