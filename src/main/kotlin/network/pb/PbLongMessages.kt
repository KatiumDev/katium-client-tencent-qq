package katium.client.qq.network.pb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
object PbLongMessages {

    @Serializable
    data class Request(
        @ProtoNumber(1) val subCommand: Int,
        @ProtoNumber(2) val termType: Int,
        @ProtoNumber(3) val platformType: Int,
        @ProtoNumber(4) val uploads: List<Upload.Request> = emptyList(),
        @ProtoNumber(5) val downloads: List<Download.Request> = emptyList(),
        @ProtoNumber(6) val deletes: List<Delete.Request> = emptyList(),
        @ProtoNumber(10) val agentType: Int,
    )

    @Serializable
    data class Response(
        @ProtoNumber(1) val subCommand: Int,
        @ProtoNumber(2) val uploads: List<Upload.Response> = emptyList(),
        @ProtoNumber(3) val downloads: List<Download.Response> = emptyList(),
        @ProtoNumber(4) val deletes: List<Delete.Response> = emptyList(),
    )

    object Upload {

        @Serializable
        data class Request(
            @ProtoNumber(1) val type: Int,
            @ProtoNumber(2) val toUin: Long,
            @ProtoNumber(3) val id: Int,
            @ProtoNumber(4) val content: ByteArray,
            @ProtoNumber(5) val storeType: Int,
            @ProtoNumber(6) val ukey: ByteArray,
            @ProtoNumber(7) val needCache: Int,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val messageID: Int,
            @ProtoNumber(3) val resourceID: ByteArray,
        )

    }

    object Download {

        @Serializable
        data class Request(
            @ProtoNumber(1) val fromUin: Int,
            @ProtoNumber(2) val resourceID: ByteArray,
            @ProtoNumber(3) val type: Int,
            @ProtoNumber(4) val needCache: Int,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val resourceID: ByteArray,
            @ProtoNumber(3) val content: ByteArray,
        )

    }

    object Delete {

        @Serializable
        data class Request(
            @ProtoNumber(1) val resourceID: ByteArray,
            @ProtoNumber(2) val type: Int,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val resourceID: ByteArray,
        )

    }

}