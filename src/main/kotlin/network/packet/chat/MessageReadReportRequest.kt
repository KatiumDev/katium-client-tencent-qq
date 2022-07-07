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
import katium.core.util.netty.heapBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class MessageReadReportRequest(
    @ProtoNumber(1) val groups: Set<Group.Request> = emptySet(),
    @ProtoNumber(2) val discussions: Set<Discussion.Request> = emptySet(),
    @ProtoNumber(3) val friend: Friend.Request? = null,
    @ProtoNumber(4) val boundUin: BoundUin.Request? = null,
) {

    companion object {

        fun create(
            client: QQClient, sequenceID: Int = client.allocPacketSequenceID(), report: MessageReadReportRequest
        ) = TransportPacket.Request.Buffered(
            client = client,
            type = TransportPacket.Type.SIMPLE,
            encryptType = TransportPacket.EncryptType.D2_KEY,
            sequenceID = sequenceID,
            command = "PbMessageSvc.PbMsgReadedReport",
            body = PooledByteBufAllocator.DEFAULT.heapBuffer(ProtoBuf.encodeToByteArray(report))
        )

    }

    @Serializable
    data class Response(
        @ProtoNumber(1) val groups: Set<Group.Response> = emptySet(),
        @ProtoNumber(2) val discussions: Set<Discussion.Response> = emptySet(),
        @ProtoNumber(3) val friend: Friend.Response? = null,
        @ProtoNumber(4) val boundUin: BoundUin.Response? = null,
    )

    object Group {

        @Serializable
        data class Request(
            @ProtoNumber(1) val groupCode: Long,
            @ProtoNumber(2) val lastReadSequence: Long,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val error: String? = null,
            @ProtoNumber(3) val groupCode: Long? = null,
            @ProtoNumber(4) val memberSequence: Long? = null,
            @ProtoNumber(5) val groupMessageSequence: Long? = null,
        )

    }

    object Discussion {

        @Serializable
        data class Request(
            @ProtoNumber(1) val confUin: Long? = null,
            @ProtoNumber(2) val lastReadSequence: Long? = null,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int? = null,
            @ProtoNumber(2) val error: String? = null,
            @ProtoNumber(3) val confUin: Long? = null,
            @ProtoNumber(4) val memberSequence: Long? = null,
            @ProtoNumber(5) val confSeq: Long? = null,
        )

    }

    object Friend {

        @Serializable
        data class Request(
            @ProtoNumber(1) val syncCookie: ByteArray,
            @ProtoNumber(2) val info: Set<UinPairReadInfo> = emptySet(),
        )

        @Serializable
        data class UinPairReadInfo(
            @ProtoNumber(1) val peerUin: Long,
            @ProtoNumber(2) val lastReadTime: Int,
            @ProtoNumber(3) val crmSig: ByteArray? = null,
            @ProtoNumber(4) val peerType: Int? = null,
            @ProtoNumber(5) val chatType: Int? = null,
            @ProtoNumber(6) val cpid: Long? = null,
            @ProtoNumber(7) val aioType: Int? = null,
            @ProtoNumber(9) val toTinyId: Long? = null,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val error: String? = null,
            @ProtoNumber(3) val syncCookie: ByteArray? = null,
        )

    }

    object BoundUin {

        @Serializable
        data class Request(
            @ProtoNumber(1) val syncCookie: ByteArray? = null,
            @ProtoNumber(2) val boundUin: Long,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val error: String? = null,
            @ProtoNumber(3) val syncCookie: ByteArray? = null,
            @ProtoNumber(4) val boundUin: Long,
        )

    }

}
