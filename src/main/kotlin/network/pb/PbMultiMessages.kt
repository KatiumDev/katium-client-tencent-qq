package katium.client.qq.network.pb

import katium.client.qq.network.message.pb.PbMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
object PbMultiMessages {

    @Serializable
    data class Request(
        @ProtoNumber(1) val subCommand: Int,
        @ProtoNumber(2) val termType: Int,
        @ProtoNumber(3) val platformType: Int,
        @ProtoNumber(4) val networkType: Int,
        @ProtoNumber(5) val buildVersion: String,
        @ProtoNumber(6) val uploads: List<Upload.Request> = emptyList(),
        @ProtoNumber(7) val downloads: List<Download.Request> = emptyList(),
        @ProtoNumber(8) val buType: Int,
        @ProtoNumber(9) val channelType: Int,
    )

    @Serializable
    data class Response(
        @ProtoNumber(1) val subCommand: Int,
        @ProtoNumber(2) val uploads: List<Upload.Response> = emptyList(),
        @ProtoNumber(3) val downloads: List<Download.Response> = emptyList(),
    )

    @Serializable
    data class ExtensionInfo(
        @ProtoNumber(1) val channelType: Int,
    )

    object Upload {

        @Serializable
        data class Request(
            @ProtoNumber(1) val toUin: Long,
            @ProtoNumber(2) val size: Long,
            @ProtoNumber(3) val md5: ByteArray,
            @ProtoNumber(4) val type: Int,
            @ProtoNumber(5) val applyId: Int,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val resourceID: String,
            @ProtoNumber(3) val ukey: ByteArray,
            @ProtoNumber(4) val ip: List<Int> = emptyList(),
            @ProtoNumber(5) val port: List<Int> = emptyList(),
            @ProtoNumber(6) val blockSize: Long? = null,
            @ProtoNumber(7) val offset: Long? = null,
            @ProtoNumber(8) val applyID: Int,
            @ProtoNumber(9) val key: ByteArray,
            @ProtoNumber(10) val sig: ByteArray,
            @ProtoNumber(11) val extension: ExtensionInfo? = null,
            @ProtoNumber(12) val ipv6: List<ByteArray> = emptyList(),
            @ProtoNumber(13) val ipv6Port: List<Int> = emptyList(),
        )

    }

    object Download {

        @Serializable
        data class Request(
            @ProtoNumber(1) val resourceID: ByteArray,
            @ProtoNumber(2) val type: Int,
            @ProtoNumber(3) val fromUin: Long,
            @ProtoNumber(4) val applyID: Int? = null,
            @ProtoNumber(5) val key: ByteArray,
            @ProtoNumber(6) val sig: ByteArray,
        )

        @Serializable
        data class Response(
            @ProtoNumber(1) val result: Int,
            @ProtoNumber(2) val thumbDownPara: ByteArray,
            @ProtoNumber(3) val key: ByteArray,
            @ProtoNumber(4) val ip: List<Int> = emptyList(),
            @ProtoNumber(5) val port: List<Int> = emptyList(),
            @ProtoNumber(6) val resourceID: ByteArray,
            @ProtoNumber(7) val extension: ExtensionInfo? = null,
            @ProtoNumber(8) val ipv6: List<ByteArray> = emptyList(),
            @ProtoNumber(9) val ipv6Port: List<Int> = emptyList(),
        )

    }

    object Highway {

        @Serializable
        data class Body(
            @ProtoNumber(1) val messages: List<PbMessage> = emptyList(),
            @ProtoNumber(2) val items: List<Item> = emptyList(),
        )

        @Serializable
        data class Item(
            @ProtoNumber(1) val fileName: String? = null,
            @ProtoNumber(2) val buffer: New? = null,
        )

        @Serializable
        data class New(
            @ProtoNumber(1) val messages: List<PbMessage> = emptyList(),
        )

    }

}