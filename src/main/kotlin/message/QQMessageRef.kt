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
package katium.client.qq.message

import katium.client.qq.QQBot
import katium.client.qq.group.QQGroup
import katium.core.message.MessageRef
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking
import java.lang.ref.SoftReference

class QQMessageRef(
    override val bot: QQBot,
    message: QQMessage?,
    val sequence: Int,
    val contextGroupCode: Long?
) : MessageRef(bot) {

    private var messageRef = SoftReference(message)

    override val message: QQMessage?
        get() {
            if (messageRef.get() == null && contextGroupCode != null) {
                runBlocking(bot.coroutineContext + CoroutineName("Refresh Group Message Ref")) {
                    messageRef = SoftReference(
                        bot.getGroup(contextGroupCode)!!.pullHistoryMessages(sequence - 5L, sequence + 5L)
                            .find { it.sequence == sequence }!!
                    )
                }
            }
            return messageRef.get()
        }

    override fun toString() = "QQMessageRef($sequence in ${contextGroupCode ?: "C2C chat"})"

}