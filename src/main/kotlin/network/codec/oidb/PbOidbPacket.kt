package katium.client.qq.network.codec.oidb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PbOidbPacket(
    @ProtoNumber(1) val command: Int,
    @ProtoNumber(2) val serviceType: Int,
    @ProtoNumber(3) val result: Int,
    @ProtoNumber(4) val buffer: ByteArray,
    @ProtoNumber(5) val error: String? = null,
    @ProtoNumber(6) val clientVersion: String? = null,
)