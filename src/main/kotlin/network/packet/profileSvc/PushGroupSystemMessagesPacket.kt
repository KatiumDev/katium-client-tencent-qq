package katium.client.qq.network.packet.profileSvc

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbSystemMessage
import katium.client.qq.review.group.QQGroupInvitationMessage
import katium.client.qq.review.group.QQJoinGroupRequestMessage
import katium.core.review.ReviewMessage
import katium.core.util.netty.toArray

class PushGroupSystemMessagesPacket(val client: QQClient, packet: TransportPacket.Response.Buffered) :
    TransportPacket.Response.Simple(packet) {

    var messages: List<ReviewMessage> = emptyList()
        private set

    override fun readBody(input: ByteBuf) {
        val payload = PbSystemMessage.PushSystemMessages.parseFrom(input.toArray(false))
        val reviewMessages = mutableListOf<ReviewMessage>()
        payload.groupMessagesList.forEach { message ->
            when (message.message.subType) {
                1, 2 -> {
                    when (message.message.groupMessageType) {
                        1 -> reviewMessages += QQJoinGroupRequestMessage(client, message)
                        2 -> reviewMessages += QQGroupInvitationMessage(client, message)
                        22 -> TODO("https://cs.github.com/Mrs4s/MiraiGo/blob/master/client/system_msg.go#L230")
                        else -> throw UnsupportedOperationException("Unknown system group message type: ${message.message.groupMessageType} $message")
                    }
                }
                3 -> {}
                5 -> {} // 自身状态变更(管理员/加群退群)
                else -> throw UnsupportedOperationException("Unknown system message sub-type: ${message.message.subType} $message")
            }
        }
        messages = reviewMessages.toList()
    }

}