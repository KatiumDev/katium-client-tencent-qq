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
import katium.client.qq.network.message.pb.PbMessageElement
import katium.core.message.content.MessageContent

interface MessageEncoder<T : MessageContent> {

    val shouldStandalone get() = false

    val priority get() = 10

    val maxCountOneMessage: Int? get() = null

    suspend fun encode(
        client: QQClient, context: QQChat, message: T, withGeneralFlags: Boolean, isStandalone: Boolean
    ): Array<PbMessageElement>

}