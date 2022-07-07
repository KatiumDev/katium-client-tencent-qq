package katium.client.qq.network.pb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
object PbD50 {

    @Serializable
    data class Request(
        @ProtoNumber(1) val appID: Long,
        @ProtoNumber(2) val maxPackageSize: Int,
        @ProtoNumber(3) val startTime: Int,
        @ProtoNumber(4) val startIndex: Int,
        @ProtoNumber(5) val requestNumber: Int,
        @ProtoNumber(6) val uinList: List<Long> = emptyList(),
        @ProtoNumber(91001) val musicSwitch: Int,
        @ProtoNumber(101001) val mutualMarkAlienation: Int,
        @ProtoNumber(141001) val mutualMarkScore: Int,
        @ProtoNumber(151001) val ksingSwitch: Int,
        @ProtoNumber(181001) val mutualMarkLbsshare: Int,
    )

}