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
package katium.client.qq.network.codec.oicq

import io.netty.buffer.ByteBuf
import katium.client.qq.network.crypto.EncryptionMethod
import katium.client.qq.network.crypto.ecdh.EcdhKeyProvider
import katium.client.qq.network.crypto.tea.QQTeaCipher
import katium.core.util.netty.buffer
import katium.core.util.netty.use
import katium.core.util.netty.writeUByteArray
import kotlin.random.Random

class OicqPacketCodec(
    val ecdh: EcdhKeyProvider
) {

    val randomKey = Random.Default.nextBytes(16).toUByteArray()
    val randomKeyCipher = QQTeaCipher(randomKey)
    var wtSessionTicketKeyCipher: QQTeaCipher? = null

    fun encode(output: ByteBuf, packet: OicqPacket.Request, release: Boolean = true) {
        output.run {
            val basePos = writerIndex()
            writeByte(0x02)
            writeZero(2) // length
            writeShort(8001)
            writeShort(packet.command.toInt())
            writeShort(1)
            writeInt(packet.uin)
            writeByte(0x03)
            writeByte(
                when (packet.encryptionMethod) {
                    EncryptionMethod.ECDH -> 0x87
                    EncryptionMethod.ST -> 0x45
                }
            )
            writeByte(0)
            writeInt(2)
            writeInt(0)
            writeInt(0)
            when (packet.encryptionMethod) {
                EncryptionMethod.ECDH -> {
                    writeByte(0x02)
                    writeByte(0x01)
                    writeUByteArray(randomKey)
                    writeShort(0x01_31)
                    writeShort(ecdh.serverKeyVersion)
                    writeShort(ecdh.clientPublicKeyEncoded.size)
                    writeBytes(ecdh.clientPublicKeyEncoded)
                    ecdh.shareKeyTeaCipher.encrypt(alloc().buffer {
                        packet.writeBody(this)
                    }).use {
                        writeBytes(it)
                    }
                }
                EncryptionMethod.ST -> {
                    writeByte(0x01)
                    writeByte(0x03)
                    writeUByteArray(randomKey)
                    writeShort(0x01_02)
                    writeShort(0x0000)
                    randomKeyCipher.encrypt(alloc().buffer {
                        packet.writeBody(this)
                    }).use {
                        writeBytes(it)
                    }
                }
            }
            writeByte(0x03)
            setShort(basePos + 1, writerIndex() - basePos)
        }
        if (release) {
            packet.close()
        }
    }

    fun decode(reader: ByteBuf, release: Boolean = true): OicqPacket.Response = reader.run {
        if (readByte().toInt() != 0x02) throw UnsupportedOperationException("Unknown flag")
        skipBytes(4)
        val command = readShort()
        skipBytes(2) // always 1
        val uin = readInt()
        skipBytes(1)
        val encryptionMethod = readByte().toInt()
        skipBytes(1)
        val bodyLength = reader.readableBytes() - 1
        val body = when (encryptionMethod) {
            0 -> {
                try {
                    ecdh.shareKeyTeaCipher.decrypt(reader.duplicate().readBytes(bodyLength))
                } catch (e: Exception) {
                    randomKeyCipher.decrypt(reader.duplicate().readBytes(bodyLength))
                }
            }
            3 -> wtSessionTicketKeyCipher!!.decrypt(reader.duplicate().readBytes(bodyLength), release = false)
            // @TODO: https://cs.github.com/mamoe/mirai/blob/dev/mirai-core/src/commonMain/kotlin/network/components/PacketCodec.kt?q=repo%3Amamoe%2Fmirai+WtSessionTicketKey#L260
            else -> throw UnsupportedOperationException("Unknown encryption method $encryptionMethod")
        }
        reader.skipBytes(bodyLength + 1)
        if (release) {
            reader.release()
        }
        // @TODO: Serializers
        return OicqPacket.Response.Buffered(uin, command, EncryptionMethod.ECDH).apply {
            readBody(body)
            body.release()
        }
    }

}