package katium.client.qq.network.pb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
/**
 * https://cs.github.com/mamoe/mirai/blob/dev/mirai-core/src/commonMain/kotlin/network/protocol/data/proto/MsgSvc.kt#L426
 */
data class RoutingHeader(
    @ProtoNumber(1) val friend: ToFriend? = null,
    @ProtoNumber(2) val group: ToGroup? = null,
) {

    @Serializable
    data class ToFriend(
        @ProtoNumber(1) val toUin: Long? = null,
    )

    @Serializable
    data class ToGroup(
        @ProtoNumber(1) val groupCode: Long? = null,
    )

}
