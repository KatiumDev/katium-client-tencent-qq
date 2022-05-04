package katium.client.qq.network.codec.struct

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.core.util.netty.buffer

fun ByteBuf.wrapUniRequestData(release: Boolean = true): ByteBuf = ByteBufAllocator.DEFAULT.buffer {
    writeByte(0x0A)
    writeBytes(this@wrapUniRequestData)
    writeByte(0x0B)
    if (release) this@wrapUniRequestData.release()
}

/**
 * Decode a UniRequestData.
 *
 * The reference counter of the ByteBuf is not changed.
 */
fun ByteBuf.decodeUniRequestData(size : Int = readableBytes() - 2): ByteBuf {
    skipBytes(1)
    val data = readSlice(size)
    skipBytes(1)
    return data
}