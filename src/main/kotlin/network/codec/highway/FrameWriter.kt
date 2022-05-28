package katium.client.qq.network.codec.highway

import io.netty.buffer.ByteBuf
import katium.client.qq.network.pb.PbHighway
import katium.core.util.netty.writeUBytes

fun ByteBuf.writeHighwayFrame(
    header: PbHighway.HighwayRequestHeader,
    body: UByteArray?
) {
    writeByte(40)
    val headerBytes = header.toByteArray()
    writeInt(headerBytes.size)
    writeInt(body?.size ?: 0)
    writeBytes(headerBytes)
    if (body != null) {
        writeUBytes(body)
    }
    writeByte(41)
}
