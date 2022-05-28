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
import katium.client.qq.network.pb.PbMessages
import katium.core.util.netty.heapBuffer

object PullMessagesRequest {

    fun create(
        client: QQClient,
        sequenceID: Int = client.allocSequenceID(),
        syncFlag: PbMessages.SyncFlag = PbMessages.SyncFlag.START,
        syncCookies: ByteString? = null
    ) =
        TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "MessageSvc.PbGetMsg",
            body = ByteBufAllocator.DEFAULT.heapBuffer(createRequest(client, syncFlag, syncCookies).toByteArray())
        )

    fun createRequest(
        client: QQClient,
        flag: PbMessages.SyncFlag,
        syncCookies: ByteString? = null
    ): PbMessages.PullMessagesRequest =
        PbMessages.PullMessagesRequest.newBuilder().apply {
            syncFlag = flag
            syncCookie = syncCookies ?: client.synchronzier.syncCookie ?: createInitialSyncCookies()
            latestRambleNumber = 20
            otherRambleNumber = 3
            onlineSyncFlag = 1
            contextFlag = 1
            requestType = 1
            publicAccountCookie = client.synchronzier.publicAccountCookie ?: ByteString.empty()
            messageControlBuffer = ByteString.empty()
            serverBuffer = ByteString.empty()
        }.build()

    fun createInitialSyncCookies(messageTime: Long = System.currentTimeMillis() / 1000): ByteString =
        PbMessages.SyncCookie.newBuilder().apply {
            time = messageTime
            ran1 = 758330138L
            ran2 = 2480149246L
            const1 = 1167238020L
            const2 = 3913056418L
            const3 = 0x1D
        }.build().toByteString()

}