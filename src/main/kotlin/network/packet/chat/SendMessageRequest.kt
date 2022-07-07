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
package katium.client.qq.network.packet.chat

import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.message.pb.PbMessage
import katium.client.qq.network.message.pb.PbMessageElement
import katium.client.qq.network.pb.RoutingHeader
import katium.client.qq.network.sync.SyncCookie
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SendMessageRequest(
    @ProtoNumber(1) val header: RoutingHeader,
    @ProtoNumber(2) val content: PbMessage.ContentHeader,
    @ProtoNumber(3) val body: PbMessage.Body,
    @ProtoNumber(4) val sequence: Int,
    @ProtoNumber(5) val random: Int,
    @ProtoNumber(6) val syncCookie: ByteArray? = null,
    @ProtoNumber(7) val appShare: AppShareInfo? = null,
    @ProtoNumber(8) val via: Int? = null,
    @ProtoNumber(9) val dataStatist: Int? = null,
    //@ProtoNumber(10) val multiMsgAssist: MultiMsgAssist?,
    //@ProtoNumber(11) val inputNotifyInfo: PbInputNotifyInfo?,
    @ProtoNumber(12) val control: MessageControl? = null,
    //@ProtoNumber(13) val receiptReq: ImReceipt.ReceiptReq?,
    @ProtoNumber(14) val multiSendSequence: Int? = null,
) {

    companion object {

        fun create(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            messageSequence: Int,
            packageNumber: Int = 1,
            packageIndex: Int = 0,
            divideSequence: Int = 0,
            routingHeader: RoutingHeader,
            elements: List<PbMessageElement>,
            messageRandom: Int,
            forward: Boolean = false,
            syncCookieTime: Long?,
        ) =
            TransportPacket.Request.Buffered(
                client = client,
                type = TransportPacket.Type.SIMPLE,
                encryptType = TransportPacket.EncryptType.D2_KEY,
                sequenceID = sequenceID,
                command = "MessageSvc.PbSendMsg",
                body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                    ProtoBuf.encodeToByteArray(
                        createRequest(
                            messageSequence,
                            packageNumber, packageIndex, divideSequence,
                            routingHeader, elements, messageRandom, forward, syncCookieTime
                        )
                    )
                )
            )

        fun createRequest(
            messageSequence: Int,
            packageNumber: Int,
            packageIndex: Int,
            divideSequence: Int,
            routingHeader: RoutingHeader,
            elements: List<PbMessageElement>,
            messageRandom: Int,
            forward: Boolean,
            syncCookieTime: Long?,
        ) = SendMessageRequest(
            header = routingHeader,
            content = PbMessage.ContentHeader(
                packageNumber = packageNumber,
                packageIndex = packageIndex,
                divideSequence = divideSequence
            ),
            body = PbMessage.Body(
                richText = PbMessage.Body.RichText(
                    elements = elements,
                    attributes = PbMessage.Body.Attributes(random = messageRandom)
                )
            ),
            random = messageRandom,
            syncCookie = if (syncCookieTime == null) ByteArray(0) else SyncCookie.createInitialSyncCookies(
                syncCookieTime
            ),
            via = 1,
            sequence = messageSequence,
            control = if (forward) MessageControl(flag = 4) else null
        )

    }

    @Serializable
    data class AppShareInfo(
        @ProtoNumber(1) val id: Int,
        @ProtoNumber(2) val cookie: ByteArray,
        @ProtoNumber(3) val resource: PluginInfo? = null,
    )

    @Serializable
    data class PluginInfo(
        @ProtoNumber(1) val resourceID: Int? = null,
        @ProtoNumber(2) val packageName: String? = null,
        @ProtoNumber(3) val newVersion: Int? = null,
        @ProtoNumber(4) val resourceType: Int? = null,
        @ProtoNumber(5) val lanType: Int? = null,
        @ProtoNumber(6) val priority: Int? = null,
        @ProtoNumber(7) val resourceName: String? = null,
        @ProtoNumber(8) val resourceDescription: String? = null,
        @ProtoNumber(9) val resourceUrlBig: String? = null,
        @ProtoNumber(10) val resourceUrlSmall: String? = null,
        @ProtoNumber(11) val resourceConfig: String? = null,
    )

    @Serializable
    data class MessageControl(
        @ProtoNumber(1) val flag: Int,
        @ProtoNumber(2) val resvResvInfo: ResvResvInfo? = null,
    )

    @Serializable
    data class ResvResvInfo(
        @ProtoNumber(1) val flag: Int,
        @ProtoNumber(2) val reserved1: ByteArray? = null,
        @ProtoNumber(3) val reserved2: Long? = null,
        @ProtoNumber(4) val reserved3: Long? = null,
        @ProtoNumber(5) val createTime: Int? = null,
        @ProtoNumber(6) val pictureHeight: Int? = null,
        @ProtoNumber(7) val pictureWidth: Int? = null,
        @ProtoNumber(8) val resvFlag: Int? = null,
    )

}