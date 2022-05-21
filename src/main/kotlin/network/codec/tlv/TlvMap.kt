package katium.client.qq.network.codec.tlv

import io.netty.buffer.ByteBuf
import katium.core.util.netty.readUShort

class TlvMap : HashMap<Short, ByteBuf>, AutoCloseable {

    constructor(initialCapacity: Int, loadFactor: Float) : super(initialCapacity, loadFactor)
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(m: MutableMap<out Short, out ByteBuf>) : super(m)

    fun release() {
        values.forEach(ByteBuf::release)
    }

    override fun close() = release()

}

fun ByteBuf.readTlvMap(tagSize: Int = 2, release: Boolean = true): TlvMap {
    val map = mutableMapOf<Short, ByteBuf>()
    while (readableBytes() >= tagSize) {
        val k = when (tagSize) {
            1 -> readByte().toInt()
            2 -> readShort().toInt()
            4 -> readInt()
            else -> throw UnsupportedOperationException("Unsupported tag size: $tagSize")
        }
        if (k == 255) {
            break
        } else {
            map[k.toShort()] = readBytes(readUShort().toInt())
        }
    }
    if (release) {
        release()
    }
    return TlvMap(map)
}
