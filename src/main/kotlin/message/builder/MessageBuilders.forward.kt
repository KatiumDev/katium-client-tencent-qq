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