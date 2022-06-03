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
package katium.client.qq.network.codec.oicq

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.crypto.ecdh.EcdhKeyProvider
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.client.qq.network.event.QQOicqDecodersInitializeEvent
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.client.qq.network.packet.login.UpdateSigResponse
import katium.core.util.event.post
import katium.core.util.netty.buffer
import katium.core.util.netty.use
import katium.core.util.netty.writeUBytes
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class OicqPacketCodec(
    val client: QQClient,
    val ecdh: EcdhKeyProvider = EcdhKeyProvider(client)
) {

    val randomKey = Random.Default.nextBytes(16).toUByteArray()
    val randomKeyCipher = QQTeaCipher(randomKey)
    var wtSessionTicketKey: ByteArray? = null
    var wtSessionTicketKeyCipher: QQTeaCipher? = null

    val decoders: Map<String, (QQClient, Int, Short) -> OicqPacket.Response.Simple> by lazy {
        val decoders = mutableMapOf<String, (QQClient, Int, Short) -> OicqPacket.Response.Simple>()
        registerBuiltinDecoders(decoders)
        runBlocking(CoroutineName("Initialize OICQ Packet Decoders")) {
            client.bot.post(QQOicqDecodersInitializeEvent(this@OicqPacketCodec, decoders))
        }
        decoders.toMap()
    }

    private fun registerBuiltinDecoders(decoders: MutableMap<String, (QQClient, Int, Short) -> OicqPacket.Response.Simple>) {
        decoders["wtlogin.login"] = ::LoginResponsePacket
        decoders["wtlogin.exchange_emp"] = ::UpdateSigResponse
    }

    fun encode(output: ByteBuf, packet: OicqPacket.Request, release: Boolean = true) {
        ecdh.oicqSessionCount.incrementAndGet()
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
                when (packet.encryption) {
                    OicqPacket.EncryptType.ECDH -> 0x87
                    OicqPacket.EncryptType.ST -> 0x45
                }
            )
            writeByte(0)
            writeInt(2)
            writeInt(0)
            writeInt(0)
            when (packet.encryption) {
                OicqPacket.EncryptType.ECDH -> {
                    writeByte(0x02)
                    writeByte(0x01)
                    writeUBytes(randomKey)
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
                OicqPacket.EncryptType.ST -> {
                    writeByte(0x01)
                    writeByte(0x03)
                    writeUBytes(randomKey)
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

    fun decode(reader: ByteBuf, transportCommand: String = "", release: Boolean = true): OicqPacket.Response =
        reader.run {
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
                else -> throw UnsupportedOperationException("Unknown encryption method $encryptionMethod")
            }
            reader.skipBytes(bodyLength + 1)
            if (release) {
                reader.release()
            }
            try {
                return (decoders[transportCommand]?.invoke(client, uin, command) ?: OicqPacket.Response.Buffered(
                    client,
                    uin,
                    command
                ))
                    .apply {
                        readBody(body)
                        body.release()
                    }
            } finally {
                ecdh.oicqSessionCount.decrementAndGet()
            }
        }

}