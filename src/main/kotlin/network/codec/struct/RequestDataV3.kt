package katium.client.qq.network.codec.struct

import io.netty.buffer.ByteBuf
import katium.client.qq.network.codec.jce.SimpleJceStruct

class RequestDataV3(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var map: MutableMap<String, ByteBuf> by map(0u)

    operator fun get(key: String) = map[key]
    operator fun set(key: String, value: ByteBuf) = map.put(key, value)

    override fun release() {
        super.release()
        map.values.forEach(ByteBuf::release)
    }

    override fun toString() = "RequestDataV3($map)"

}

fun RequestDataV3(vararg pairs: Pair<String, ByteBuf>) = RequestDataV3().apply {
    map.putAll(pairs)
}
