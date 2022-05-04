package katium.client.qq.network.codec.struct

import io.netty.buffer.ByteBuf

inline fun ByteBuf.writeIntLvPacket(crossinline writer: ByteBuf.() -> Unit): ByteBuf {
    val pos = writerIndex()
    writeZero(4)
    writer()
    setInt(pos, writerIndex() - pos - 4)
    return this
}
