package katium.client.qq.network.packet.configPushSvc

import io.netty.buffer.ByteBuf
import katium.client.qq.network.codec.jce.SimpleJceStruct

class FileStorageConfigPushData(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var uploadList: MutableList<SimpleJceStruct> by list(0u)
    var pictureDownloadList: MutableList<SimpleJceStruct> by list(1u)
    var gpictureDownloadList: MutableList<SimpleJceStruct> by list(2u)
    var qZoneProxyServiceList: MutableList<SimpleJceStruct> by list(3u)
    var urlEncodeServiceList: MutableList<SimpleJceStruct> by list(4u)
    var bigDataChannel: BigDataChannel by struct(5u, BigDataChannel::class)
    var vipEmotionList: MutableList<SimpleJceStruct> by list(6u)
    var c2cPictureDownloadList: MutableList<SimpleJceStruct> by list(7u)
    var fmtIPInfo: SimpleJceStruct by struct(8u, SimpleJceStruct::class)
    var domainIPChannel: SimpleJceStruct by struct(9u, SimpleJceStruct::class)
    var pttList: ByteBuf by byteBuf(10u)

    override fun release() {
        super.release()
        bigDataChannel.release()
        pttList.release()
    }

    class Address(other: SimpleJceStruct) : SimpleJceStruct(other) {

        constructor() : this(SimpleJceStruct())
        constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

        var address: String by string(1u)
        var port: Int by number(2u)

        override fun toString() = "$address:$port"

    }

    class BigDataChannel(other: SimpleJceStruct) : SimpleJceStruct(other) {

        constructor() : this(SimpleJceStruct())
        constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

        var ipLists: MutableList<SimpleJceStruct> by list(0u)
        var sigSession: ByteBuf by byteBuf(1u)
        var keySession: ByteBuf by byteBuf(2u)
        var sigUin: Long by number(3u)
        var connectFlag: Int by number(4u)
        var buffer: ByteBuf by byteBuf(5u)

        override fun release() {
            super.release()
            sigSession.release()
            keySession.release()
            buffer.release()
        }

    }

}