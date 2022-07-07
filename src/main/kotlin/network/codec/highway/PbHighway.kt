package katium.client.qq.network.codec.highway

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
object PbHighway {

    @Serializable
    data class RequestHeader(
        @ProtoNumber(1) val data: DataHeader,
        @ProtoNumber(2) val segment: SegmentHeader? = null,
        @ProtoNumber(3) val extensionInfo: ByteArray? = null,
        @ProtoNumber(4) val timestamp: Long? = null,
        //@ProtoNumber(5) val msgLoginSigHead: LoginSigHead?,
    )

    @Serializable
    data class ResponseHeader(
        @ProtoNumber(1) val data: DataHeader,
        @ProtoNumber(2) val segment: SegmentHeader? = null,
        @ProtoNumber(3) val errorCode: Int,
        @ProtoNumber(4) val allowRetry: Int? = null,
        @ProtoNumber(5) val cacheCost: Int? = null,
        @ProtoNumber(6) val htCost: Int? = null,
        @ProtoNumber(7) val extensionInfo: ByteArray? = null,
        @ProtoNumber(8) val timestamp: Long? = null,
        @ProtoNumber(9) val range: Long? = null,
        @ProtoNumber(10) val isReset: Int? = null,
    )

    @Serializable
    data class DataHeader(
        @ProtoNumber(1) val version: Int,
        @ProtoNumber(2) val uin: String,
        @ProtoNumber(3) val command: String,
        @ProtoNumber(4) val sequence: Int,
        @ProtoNumber(5) val retryTimes: Int,
        @ProtoNumber(6) val appID: Int,
        @ProtoNumber(7) val flag: Int,
        @ProtoNumber(8) val commandID: Int,
        @ProtoNumber(9) val buildVersion: String,
        @ProtoNumber(10) val localeID: Int,
    )

    @Serializable
    data class SegmentHeader(
        @ProtoNumber(1) val serviceID: Int = 0,
        @ProtoNumber(2) val fileSize: Long = 0,
        @ProtoNumber(3) val dataOffset: Long = 0,
        @ProtoNumber(4) val dataLength: Int = 0,
        @ProtoNumber(5) val rtCode: Int = 0,
        @ProtoNumber(6) val serviceTicket: ByteArray = ByteArray(0),
        @ProtoNumber(7) val flag: Int = 0,
        @ProtoNumber(8) val md5: ByteArray = ByteArray(0),
        @ProtoNumber(9) val fileMd5: ByteArray = ByteArray(0),
        @ProtoNumber(10) val cacheAddress: Int = 0,
        @ProtoNumber(11) val queryTimes: Int = 0,
        @ProtoNumber(12) val updateCacheIP: Int = 0,
    )

}