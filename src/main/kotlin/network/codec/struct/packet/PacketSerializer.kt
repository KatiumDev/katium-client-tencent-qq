/*
 * Katium Client Tencent QQ: Tencent QQ protocol implementation for Katium
 * Copyright (C) 2022  Katium Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package katium.client.qq.network.codec.struct.packet

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.crypto.EncryptType
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.client.qq.network.codec.struct.readQQIntLengthString
import katium.client.qq.network.codec.struct.writeQQIntLengthString
import katium.client.qq.network.codec.struct.writeWithIntLength
import katium.core.util.netty.*
import java.io.ByteArrayInputStream
import java.util.zip.InflaterInputStream
import kotlin.math.min

private val EMPTY_KEY = UIntArray(4)

fun ByteBuf.writePacket(client: QQClient, packet: RequestPacket, release: Boolean = true): ByteBuf {
    val encryptType = if (client.sig.d2.isEmpty()) EncryptType.EMPTY_KEY else packet.encryptType
    writeWithIntLength {
        run { // Head
            writeUInt(packet.type.value)
            writeUByte(encryptType.value)
            when (packet.type) {
                PacketType.LOGIN -> {
                    when (encryptType) {
                        EncryptType.D2_KEY -> {
                            writeInt(client.sig.d2.size + 4)
                            writeUByteArray(client.sig.d2)
                        }
                        else -> {
                            writeInt(4)
                        }
                    }
                }
                PacketType.SIMPLE -> {
                    writeInt(packet.sequenceID)
                }
            }
            writeByte(0)
            writeQQIntLengthString(packet.uin.toString())
        }
        run { // Body
            val body = alloc().buffer().writePacketBody(client, packet, release)
            when (encryptType) {
                EncryptType.NONE -> body
                else -> {
                    QQTeaCipher(
                        *when (encryptType) {
                            EncryptType.D2_KEY -> client.sig.d2Key!!
                            EncryptType.EMPTY_KEY -> EMPTY_KEY
                            else -> throw IllegalStateException()
                        }
                    ).encrypt(body)
                }
            }.use {
                writeBytes(it)
            }
        }
    }
    return this
}

fun ByteBuf.writePacketBody(client: QQClient, packet: RequestPacket, release: Boolean = true): ByteBuf {
    writeWithIntLength {
        if (packet.type == PacketType.LOGIN) {
            writeInt(packet.sequenceID)
            writeInt(client.clientVersion.appID)
            writeInt(client.clientVersion.subAppID)
            writeBytes(byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00))
            writeWithIntLength(client.sig.tgt)
        }
        writeQQIntLengthString(packet.command)
        writeWithIntLength(client.sig.outgoingPacketSessionId)
        if (packet.type == PacketType.LOGIN) {
            writeQQIntLengthString(client.deviceInfo.IMEI)
            writeInt(0x04)
            writeShort(client.sig.ksid.size + 2)
            writeUByteArray(client.sig.ksid)
        }
        writeInt(0x04)
    }
    writeWithIntLength(packet.body)
    if (release) packet.close()
    return this
}

/**
 * Read a response packet without header length field
 *
 * Return NULL if the packet is a `Heartbeat.Alive` packet
 */
fun ByteBuf.readPacket(client: QQClient, release: Boolean = true): ResponsePacket? {
    val type = PacketType.of(readUInt())
    val encryptType = EncryptType.of(readUByte())
    skipBytes(1) // always 0x00
    val uin = readQQIntLengthString().toLong()
    val data = when (encryptType) {
        EncryptType.NONE -> this.retainedDuplicate()
        EncryptType.D2_KEY -> QQTeaCipher(*client.sig.d2Key!!).decrypt(this, release = false)
        EncryptType.EMPTY_KEY -> QQTeaCipher(*EMPTY_KEY).decrypt(this, release = false)
    }
    if (release) {
        this.release()
    }
    return data.readSSOFrame(type, encryptType, uin)
}

/**
 * Read a decrypted response packet body
 *
 * Return NULL if the packet is a `Heartbeat.Alive` packet
 */
fun ByteBuf.readSSOFrame(
    type: PacketType,
    encryptType: EncryptType,
    uin: Long,
    release: Boolean = true
): ResponsePacket? {
    val headerLength = readInt() - 4
    val header = readSlice(headerLength)
    val sequenceID = header.readInt()
    when (val returnCode = header.readInt()) {
        0 -> {}
        -10008 -> throw IllegalStateException("Session expired")
        else -> throw IllegalStateException("Unknown return code: $returnCode")
    }
    val message = header.readQQIntLengthString()
    val command = header.readQQIntLengthString()
    if (command == "Heartbeat.Alive") {
        return null
    }
    header.skipBytes(header.readInt() - 4) // session ID
    val compressFlag = header.readInt()

    val body = readBytes(min(readInt() - 4, readableBytes()))
    if (release) {
        release()
    }
    return ResponsePacket(
        type = type,
        encryptType = encryptType,
        uin = uin,
        sequenceID = sequenceID,
        command = command,
        body = if (compressFlag == 1)
            alloc().buffer(InflaterInputStream(ByteArrayInputStream(body.toArray(true))))
        else body,
        message = message
    )
}
