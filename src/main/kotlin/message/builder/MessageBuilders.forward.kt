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
@file:Suppress("FunctionName") @file:JvmName("QQForward")

package katium.client.qq.message.builder

import katium.client.qq.message.content.QQForward
import katium.core.message.MessageRef

@JvmName("ofUrlShare")
@JvmOverloads
fun QQForward(
    messages: List<MessageRef>,
    title: String = "群聊的聊天记录",
    brief: String = "[聊天记录]",
    preview: String? = null,
    summary: String = "查看 ${messages.size} 条转发消息",
    source: String = "聊天记录"
) = QQForward(messages, title, brief, preview, summary, source)