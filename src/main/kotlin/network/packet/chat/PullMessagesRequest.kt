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
import katium.client.qq.network.sync.SyncCookie
import katium.client.qq.network.sync.SyncFlag
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PullMessagesRequest(
    @ProtoNumber(1) val syncFlag: SyncFlag? = null,
    @ProtoNumber(2) val syncCookie: ByteArray? = null,
    @ProtoNumber(3) val rambleFlag: Int? = null,
    @ProtoNumber(4) val latestRambleNumber: Int? = null,
    @ProtoNumber(5) val otherRambleNumber: Int? = null,
    @ProtoNumber(6) val onlineSyncFlag: Int? = null,
    @ProtoNumber(7) val contextFlag: Int? = null,
    @ProtoNumber(8) val whisperSessionId: Int? = null,
    @ProtoNumber(9) val requestType: Int? = null,
    @ProtoNumber(10) val publicAccountCookie: ByteArray? = null,
    @ProtoNumber(11) val messageControlBuffer: ByteArray? = null,
    @ProtoNumber(12) val serverBuffer: ByteArray? = null,
) {

    companion object {

        fun create(
            client: QQClient,
            sequenceID: Int = client.allocPacketSequenceID(),
            syncFlag: SyncFlag = SyncFlag.START,
            syncCookies: ByteArray? = null
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "MessageSvc.PbGetMsg",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(
                ProtoBuf.encodeToByteArray(
                    createRequest(
                        client,
                        syncFlag,
                        syncCookies
                    )
                )
            )
        )

        fun createRequest(
            client: QQClient, flag: SyncFlag, syncCookies: ByteArray? = null
        ) = PullMessagesRequest(
            syncFlag = flag,
            syncCookie = syncCookies ?: client.synchronzier.syncCookie ?: SyncCookie.createInitialSyncCookies(),
            latestRambleNumber = 20,
            otherRambleNumber = 3,
            onlineSyncFlag = 1,
            contextFlag = 1,
            requestType = 1,
            publicAccountCookie = client.synchronzier.publicAccountCookie ?: ByteArray(0),
            messageControlBuffer = ByteArray(0),
            serverBuffer = ByteArray(0),
        )

    }

}