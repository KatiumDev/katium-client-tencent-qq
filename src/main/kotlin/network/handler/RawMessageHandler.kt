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

import katium.client.qq.network.event.QQReceivedRawMessageEvent
import katium.core.event.MessageReceivedEvent
import katium.core.event.MessageSentEvent
import katium.core.util.event.AsyncMode
import katium.core.util.event.Subscribe
import katium.core.util.event.post

object RawMessageHandler : QQClientHandler {

    override val id: String get() = "raw_messages_decoder"

    @Subscribe(async = AsyncMode.ASYNC)
    suspend fun onMessage(event: QQReceivedRawMessageEvent) {
        val (_, client, message) = event
        val parsedMessage = (client.messageParsers[message.header.type]
            ?: throw UnsupportedOperationException("Unknown message type: ${message.header.type}"))
            .parse(client, message)
        if (message.header.fromUin == client.uin) {
            client.bot.post(MessageSentEvent(parsedMessage))
        } else {
            client.bot.post(MessageReceivedEvent(parsedMessage))
        }
    }

}