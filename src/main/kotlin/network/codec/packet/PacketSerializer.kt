/*
 * Copyright 2022 Katium Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package katium.client.qq.network.codec.packet

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.base.readQQIntLengthString
import katium.client.qq.network.codec.base.writeQQIntLengthString
import katium.client.qq.network.codec.base.writeWithIntLength
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.core.util.netty.*
import java.io.ByteArrayInputStream
import java.util.zip.InflaterInputStream
import kotlin.math.min

private val EMPTY_KEY = UIntArray(4)

fun ByteBuf.writePacket(client: QQClient, packet: TransportPacket.Request, release: Boolean = true): ByteBuf {
    val encryptType = if (client.sig.d2.isEmpty()) TransportPacket.EncryptType.EMPTY_KEY else packet.encryptType
    writeWithIntLength {
        run { // Head
            writeUInt(packet.type.value)
            writeUByte(encryptType.value)
            when (packet.type) {
                TransportPacket.Type.LOGIN -> {
                    when (encryptType) {
                        TransportPacket.EncryptType.D2_KEY -> {
                            writeInt(client.sig.d2.size + 4)
                            writeBytes(client.sig.d2)
                        }
                        else -> {
                            writeInt(4)
                        }
                    }
                }
                TransportPacket.Type.SIMPLE -> {
                    writeInt(packet.sequenceID)
                }
            }
            writeByte(0)
            writeQQIntLengthString(packet.uin.toString(), true)
        }
        run { // Body
            val body = alloc().buffer().writePacketBody(client, packet, release)
            when (encryptType) {
                TransportPacket.EncryptType.NONE -> body
                else -> {
                    QQTeaCipher(
                        *when (encryptType) {
                            TransportPacket.EncryptType.D2_KEY -> synchronized(client.sig) { client.sig.d2Key }
                            TransportPacket.EncryptType.EMPTY_KEY -> EMPTY_KEY
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

fun ByteBuf.writePacketBody(client: QQClient, packet: TransportPacket.Request, release: Boolean = true): ByteBuf {
    writeWithIntLength {
        if (packet.type == TransportPacket.Type.LOGIN) {
            writeInt(packet.sequenceID)
            writeInt(client.version.appID)
            writeInt(client.version.subAppID)
            writeBytes(byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00))
            writeWithIntLength(client.sig.tgt)
        }
        writeQQIntLengthString(packet.command, true)
        writeWithIntLength(client.sig.outgoingPacketSessionId)
        if (packet.type == TransportPacket.Type.LOGIN) {
            writeQQIntLengthString(client.deviceInfo.IMEI, true)
            writeInt(0x04)
            writeShort(client.sig.ksid.size + 2)
            writeBytes(client.sig.ksid)
        }
        writeInt(0x04)
    }
    writeWithIntLength {
        packet.writeBody(this)
    }
    if (release) packet.close()
    return this
}

/**
 * Read a response packet without header length field
 *
 * Return NULL if the packet is a `Heartbeat.Alive` packet
 */
fun ByteBuf.readPacket(client: QQClient, release: Boolean = true): TransportPacket.Response? {
    val type = TransportPacket.Type.of(readUInt())
    val encryptType = TransportPacket.EncryptType.of(readUByte())
    skipBytes(1) // always 0x00
    val uin = readQQIntLengthString(true).toLong()
    val data = when (encryptType) {
        TransportPacket.EncryptType.NONE -> this.retainedDuplicate()
        TransportPacket.EncryptType.D2_KEY -> synchronized(client.sig) {
            QQTeaCipher(*client.sig.d2Key).decrypt(
                this,
                release = false
            )
        }
        TransportPacket.EncryptType.EMPTY_KEY -> QQTeaCipher(*EMPTY_KEY).decrypt(this, release = false)
    }
    if (release) {
        this.release()
    }
    return data.readSSOFrame(client, type, encryptType, uin)
}

/**
 * Read a decrypted response packet body
 *
 * Return NULL if the packet is a `Heartbeat.Alive` packet
 */
fun ByteBuf.readSSOFrame(
    client: QQClient,
    type: TransportPacket.Type,
    encryptType: TransportPacket.EncryptType,
    uin: Long,
    release: Boolean = true
): TransportPacket.Response? {
    val headerLength = readInt() - 4
    val header = readSlice(headerLength)
    val sequenceID = header.readInt()
    when (val returnCode = header.readInt()) {
        0 -> {}
        -10008 -> throw IllegalStateException("Session expired")
        else -> throw IllegalStateException("Unknown return code: $returnCode")
    }
    val message = header.readQQIntLengthString(true)
    val command = header.readQQIntLengthString(true)
    if (command == "Heartbeat.Alive") {
        return null
    }
    header.skipBytes(header.readInt() - 4) // session ID
    val compressFlag = header.readInt()

    val body = readBytes(min(readInt() - 4, readableBytes()))
    if (release) {
        release()
    }
    val uncompressedBody = if (compressFlag == 1)
        alloc().buffer(InflaterInputStream(ByteArrayInputStream(body.toArray(true))))
    else body
    return if (encryptType == TransportPacket.EncryptType.EMPTY_KEY) {
        TransportPacket.Response.Oicq(client, type, encryptType, uin, sequenceID, command, message)
    } else {
        TransportPacket.Response.Buffered(type, encryptType, uin, sequenceID, command, message)
    }.apply {
        readBody(uncompressedBody)
        uncompressedBody.release()
    }
}
