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
package katium.client.qq.network.message.encoder

import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.core.message.content.MessageChain

object MessageChainEncoder : MessageEncoder<MessageChain> {

    override suspend fun encode(
        client: QQClient,
        context: QQChat,
        message: MessageChain
    ) = message.parts.flatMap { client.messageEncoders.encode(context, it) }.toTypedArray()

    override suspend fun createGeneralFlags(
        client: QQClient,
        context: QQChat,
        message: MessageChain
    ) = message.parts.flatMap { client.messageEncoders.createGeneralFlags(context, it) }.toTypedArray()

}