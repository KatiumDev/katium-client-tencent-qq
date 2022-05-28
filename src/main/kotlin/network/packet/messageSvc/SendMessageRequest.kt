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
package katium.client.qq.network.packet.messageSvc

import com.google.protobuf.ByteString
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.pb.PbMessageElements
import katium.client.qq.network.pb.PbMessagePackets
import katium.client.qq.network.pb.PbMessagePackets.RoutingHeader
import katium.client.qq.network.pb.PbMessages
import katium.core.util.netty.heapBuffer
import kotlin.random.Random

object SendMessageRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocPacketSequenceID(),
        messageSequence: Int = client.allocGroupMessageSequenceID(),
        packageNumber: Int = 1,
        packageIndex: Int = 0,
        divideSequence: Int = 0,
        routingHeader: RoutingHeader,
        elements: Collection<PbMessageElements.Element>,
        messageRandom: Int = Random.Default.nextInt(),
        forward: Boolean = false,
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "MessageSvc.PbSendMsg",
            body = ByteBufAllocator.DEFAULT.heapBuffer(
                createRequest(
                    messageSequence,
                    packageNumber, packageIndex, divideSequence,
                    routingHeader, elements, messageRandom, forward
                ).toByteArray()
            )
        )

    fun createRequest(
        messageSequence: Int,
        packageNumber: Int,
        packageIndex: Int,
        divideSequence: Int,
        routingHeader: RoutingHeader,
        elements: Collection<PbMessageElements.Element>,
        messageRandom: Int,
        forward: Boolean,
    ): PbMessagePackets.SendMessageRequest =
        PbMessagePackets.SendMessageRequest.newBuilder().apply {
            header = routingHeader
            content = PbMessages.ContentHeader.newBuilder().apply {
                this.packageNumber = packageNumber
                this.packageIndex = packageIndex
                this.divideSequence = divideSequence
            }.build()
            body = PbMessages.MessageBody.newBuilder().apply {
                richText = PbMessages.RichText.newBuilder().addAllElements(elements).build()
            }.build()
            random = messageRandom
            syncCookie = ByteString.empty()
            via = 1
            sequence = messageSequence
            if (forward) {
                control = PbMessagePackets.MessageControl.newBuilder().apply {
                    flag = 4
                }.build()
            }
        }.build()

}