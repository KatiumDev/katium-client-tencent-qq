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
import katium.client.qq.network.message.decoder.MessageDecoder
import katium.core.event.BotEvent

class QQMessageDecodersInitializeEvent(
    val client: QQClient,
    val decoders: MutableMap<Int, MessageDecoder>
) : BotEvent(client.bot) {

    operator fun component2() = client
    operator fun component3() = decoders

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQMessageDecodersInitializeEvent) return false
        if (!super.equals(other)) return false
        if (client != other.client) return false
        if (decoders != other.decoders) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + decoders.hashCode()
        return result
    }

    override fun toString() = "QQMessageDecodersInitializeEvent(bot=$bot, client=$client, decoders=$decoders)"

}