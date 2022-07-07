package katium.client.qq.network.sync

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SyncCookie(
    @ProtoNumber(1) val time1: Long? = null,
    @ProtoNumber(2) val time: Long? = null,
    @ProtoNumber(3) val ran1: Long? = null,
    @ProtoNumber(4) val ran2: Long? = null,
    @ProtoNumber(5) val const1: Long? = null,
    @ProtoNumber(11) val const2: Long? = null,
    @ProtoNumber(12) val const3: Long? = null,
    @ProtoNumber(13) val lastSyncTime: Long? = null,
    @ProtoNumber(14) val const4: Long? = null,
) {

    companion object {

        fun createInitialSyncCookies(messageTime: Long = System.currentTimeMillis() / 1000) =
            ProtoBuf.encodeToByteArray(
                SyncCookie(
                    time = messageTime,
                    ran1 = 758330138L,
                    ran2 = 2480149246L,
                    const1 = 1167238020L,
                    const2 = 3913056418L,
                    const3 = 0x1D
                )
            )

    }

}
