package katium.client.qq.network.sso

import katium.client.qq.network.codec.jce.SimpleJceStruct

class SsoServerRecord(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var address: String by string(1u)
    var port: Int by number(2u)
    var unknown3: Byte by number(3u)
    var unknown4: Byte by number(4u)
    var protocolType: Byte by number(5u) // 0,1: Socket 2,3: HTTP, always 0 in `2` list of the response data
    var unknown6: Int by number(6u)
    var unknown7: Byte by number(7u)
    var location: String by string(8u)
    var unknown9: String by string(9u)

    /**
     * Returned by the server, but the client does not decode it.
     */
    var unknown10: Int by number(10u)

    override fun toString() =
        "SsoServerRecord(address='$address', port=$port, unknown3=$unknown3, unknown4=$unknown4, protocolType=$protocolType, " +
                "unknown6=$unknown6, unknown7=$unknown7, location='$location', unknown9='$unknown9', unknown10=$unknown10)"

}