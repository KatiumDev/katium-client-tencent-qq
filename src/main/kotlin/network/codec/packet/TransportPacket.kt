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
package katium.client.qq.network.codec.packet

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.oicq.OicqPacketCodec
import katium.client.qq.network.crypto.EncryptType

class TransportPacket private constructor() {

    interface Request : AutoCloseable {

        val type: Type
        val encryptType: EncryptType
        val uin: Long
        val sequenceID: Int
        val command: String

        fun writeBody(output: ByteBuf)

        override fun close() {
        }

        fun component1() = type
        fun component2() = sequenceID
        fun component3() = command

        abstract class Simple(
            val client: QQClient,
            override val type: Type = Type.SIMPLE,
            override val encryptType: EncryptType,
            override val uin: Long = client.uin,
            override val sequenceID: Int = client.allocSequenceID(),
            override val command: String
        ) : Request

        open class Buffered(
            client: QQClient,
            type: Type = Type.SIMPLE,
            encryptType: EncryptType,
            uin: Long = client.uin,
            sequenceID: Int = client.allocSequenceID(),
            command: String,
            val body: ByteBuf
        ) : Simple(client, type, encryptType, uin, sequenceID, command) {

            fun component4() = body

            override fun writeBody(output: ByteBuf) {
                output.writeBytes(body)
            }

            override fun close() {
                super.close()
                body.release()
            }

        }

        open class Oicq(
            client: QQClient,
            type: Type = Type.SIMPLE,
            encryptType: EncryptType,
            uin: Long = client.uin,
            sequenceID: Int,
            command: String,
            val packet: OicqPacket.Request,
            val codec: OicqPacketCodec = client.oicqCodec
        ) : Simple(client, type, encryptType, uin, sequenceID, command) {

            fun component4() = packet

            override fun writeBody(output: ByteBuf) {
                codec.encode(output, packet, release = false)
            }

            override fun close() {
                super.close()
                packet.close()
            }

        }

    }

    interface Response : AutoCloseable {

        val type: Type
        val encryptType: EncryptType
        val uin: Long
        val sequenceID: Int
        val command: String
        val message: String

        fun component1() = type
        fun component2() = sequenceID
        fun component3() = command

        fun readBody(input: ByteBuf)

        override fun close() {
        }

        abstract class Simple(
            override val type: Type,
            override val encryptType: EncryptType,
            override val uin: Long,
            override val sequenceID: Int,
            override val command: String,
            override val message: String
        ) : Response

        open class Buffered(
            type: Type,
            encryptType: EncryptType,
            uin: Long,
            sequenceID: Int,
            command: String,
            message: String,
        ) : Simple(type, encryptType, uin, sequenceID, command, message) {

            lateinit var body: ByteBuf

            fun component4() = body

            override fun readBody(input: ByteBuf) {
                body = input.readBytes(input.readableBytes())
            }

            override fun close() {
                super.close()
                body.release()
            }

        }

        open class Oicq(
            client: QQClient,
            type: Type,
            encryptType: EncryptType,
            uin: Long,
            sequenceID: Int,
            command: String,
            message: String,
            val codec: OicqPacketCodec = client.oicqCodec
        ) : Simple(type, encryptType, uin, sequenceID, command, message) {

            lateinit var packet: OicqPacket.Response

            fun component4() = packet

            override fun readBody(input: ByteBuf) {
                packet = codec.decode(input, transportCommand = command, release = false)
            }

            override fun close() {
                super.close()
                packet.close()
            }

        }

    }

    enum class Type(val value: UInt) {

        LOGIN(0x0Au), SIMPLE(0x0Bu);

        companion object {

            fun of(value: UInt) = values().first { it.value == value }

        }

    }

}