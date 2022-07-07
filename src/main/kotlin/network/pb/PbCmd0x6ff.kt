package katium.client.qq.network.pb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoType

@OptIn(ExperimentalSerializationApi::class)
object PbCmd0x6ff {

    @Serializable
    data class Response(
        @ProtoNumber(1281) val body: SubCommand501.Response,
    )

    object SubCommand501 {

        @Serializable
        data class Response(
            @ProtoNumber(1) val sigSession: ByteArray,
            @ProtoNumber(2) val sessionKey: ByteArray,
            @ProtoNumber(3) val addresses: Set<ServerAddresses> = emptySet(),
        ) {


            @Serializable
            data class ServerAddresses(
                @ProtoNumber(1) val serviceType: Int,
                @ProtoNumber(2) val addresses: Set<ServerAddress> = emptySet(),
            )

            @Serializable
            data class ServerAddress(
                @ProtoNumber(1) val type: Int? = null,
                @ProtoNumber(2) @ProtoType(ProtoIntegerType.FIXED) val ip: Int,
                @ProtoNumber(3) val port: Int,
                @ProtoNumber(4) val area: Int? = null,
            )

        }

    }

}