package katium.client.qq.network.packet.statSvc

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.jce.SimpleJceStruct
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.taf.RequestDataV3
import katium.client.qq.network.codec.taf.RequestPacket
import katium.client.qq.network.codec.taf.wrapUniRequestData
import katium.core.util.netty.buffer
import katium.core.util.netty.heapBuffer
import java.util.*

class ClientRegisterPacket(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    companion object {

        fun create(client: QQClient, sequenceID: Int = client.allocSequenceID()) =
            TransportPacket.Request.Buffered(
                client = client,
                type = TransportPacket.Type.LOGIN,
                encryptType = TransportPacket.EncryptType.D2_KEY,
                sequenceID = sequenceID,
                command = "StatSvc.register",
                body = createRequestPacket(client).dump()
            )

        fun createRequestPacket(client: QQClient) = RequestPacket(
            version = 3,
            servantName = "PushService",
            functionName = "SvcReqRegister",
            buffer = RequestDataV3(
                "SvcReqRegister" to ClientRegisterPacket().apply {
                    uin = client.uin
                    osVersionNumber = client.deviceInfo.version.sdk.toLong()
                    guid = ByteBufAllocator.DEFAULT.buffer(client.deviceInfo.guid)
                    deviceName = client.deviceInfo.model
                    deviceType = client.deviceInfo.model
                    osVersionString = client.deviceInfo.version.release
                    vendorName = client.deviceInfo.vendorName
                    vendorOSName = client.deviceInfo.vendorOSName
                }.dump().wrapUniRequestData()
            ).dump()
        )
    }

    var uin: Long by number(0u)
    var bid: Long by number(1u, 1 or 2 or 4)
    var connType: Byte by number(2u, 0)
    var other: String by string(3u)
    var status: Int by number(4u, 11)
    var onlinePush: Byte by number(5u)
    var isOnline: Byte by number(6u)
    var isShowOnline: Byte by number(7u)
    var kickPC: Byte by number(8u, 0)
    var kickWeak: Byte by number(9u, 0)
    var timestamp: Long by number(10u)
    var osVersionNumber: Long by number(11u)
    var networkType: Byte by number(12u, 1)
    var buildVersion: String by string(13u)
    var registerType: Byte by number(14u, 0)
    var deviceParam: ByteBuf by byteBuf(15u)
    var guid: ByteBuf by byteBuf(16u)
    var localeID: Int by number(17u, 2052)
    var silentPush: Byte by number(18u)
    var deviceName: String by string(19u)
    var deviceType: String by string(20u)
    var osVersionString: String by string(21u)
    var openPush: Byte by number(22u, 1)
    var largeSequence: Long by number(23u, 1551)
    var lastWatchStartTime: Long by number(24u)
    var oldSSOIP: Long by number(26u, 0)
    var newSSOIP: Long by number(27u, 31806887127679168)
    var channelNo: String by string(28u, "")
    var cpid: Long by number(29u, 0)
    var vendorName: String by string(30u)
    var vendorOSName: String by string(31u)
    var osIdfa: String by string(32u)
    var b769: ByteBuf by field(33u) {
        ByteBufAllocator.DEFAULT.heapBuffer(HexFormat.of().parseHex("0A04082E10000A05089B021000"))
    }
    var isSetStatus: Byte by number(34u, 0)
    var serverBuf: ByteBuf by byteBuf(35u)
    var setMute: Byte by number(36u, 0)
    var extensionOnlineStatus: Long by number(38u)
    var batteryStatus: Int by number(39u)

    override fun release() {
        super.release()
        deviceParam.release()
        guid.release()
        b769.release()
        serverBuf.release()
    }

    override fun toString() = "ClientRegisterPacket(uin=$uin, bid=$bid, connType=$connType, other='$other', " +
            "status=$status, onlinePush=$onlinePush, isOnline=$isOnline, isShowOnline=$isShowOnline, kickPC=$kickPC, " +
            "kickWeak=$kickWeak, timestamp=$timestamp, osVersionNumber=$osVersionNumber, networkType=$networkType, " +
            "buildVersion='$buildVersion', registerType=$registerType, deviceParam=$deviceParam, guid=$guid, " +
            "localeID=$localeID, silentPush=$silentPush, deviceName='$deviceName', deviceType='$deviceType', " +
            "osVersionString='$osVersionString', openPush=$openPush, largeSequence=$largeSequence, " +
            "lastWatchStartTime=$lastWatchStartTime, oldSSOIP=$oldSSOIP, newSSOIP=$newSSOIP, channelNo='$channelNo', " +
            "cpid=$cpid, vendorName='$vendorName', vendorOSName='$vendorOSName', osIdfa='$osIdfa', b769=$b769, " +
            "isSetStatus=$isSetStatus, serverBuf=$serverBuf, setMute=$setMute, extensionOnlineStatus=$extensionOnlineStatus, " +
            "batteryStatus=$batteryStatus)"

}