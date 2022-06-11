package katium.client.qq.message.content

import katium.core.message.MessageRef
import katium.core.message.content.Forward

class QQForward(
    messages: List<MessageRef>,
    val title: String,
    val brief: String,
    val preview: String?,
    val summary: String,
    val source: String,
) : Forward(messages)
