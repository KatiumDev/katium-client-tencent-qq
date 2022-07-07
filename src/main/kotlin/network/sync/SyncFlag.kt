package katium.client.qq.network.sync

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
enum class SyncFlag {

    @ProtoNumber(0)
    START,

    @ProtoNumber(1)
    CONTINUE,

    @ProtoNumber(2)
    STOP,

}