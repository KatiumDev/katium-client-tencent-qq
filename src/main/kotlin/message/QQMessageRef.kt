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

import katium.core.Bot
import katium.core.message.Message
import katium.core.message.MessageRef
import java.lang.ref.WeakReference

class QQMessageRef(override val bot: Bot, message: Message) : MessageRef {

    private val messageRef = WeakReference(message)

    override val message: Message?
        get() = messageRef.get()

}