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
import katium.client.qq.network.codec.oicq.OicqPacket
import katium.client.qq.network.codec.oicq.OicqPacketCodec

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

        operator fun component1() = type
        operator fun component2() = sequenceID
        operator fun component3() = command

        abstract class Simple(
            val client: QQClient,
            override val type: Type = Type.SIMPLE,
            override val encryptType: EncryptType,
            override val uin: Long = client.uin,
            override val sequenceID: Int = client.allocPacketSequenceID(),
            override val command: String
        ) : Request

        open class Buffered(
            client: QQClient,
            type: Type = Type.SIMPLE,
            encryptType: EncryptType,
            uin: Long = client.uin,
            sequenceID: Int = client.allocPacketSequenceID(),
            command: String,
            val body: ByteBuf
        ) : Simple(client, type, encryptType, uin, sequenceID, command) {

            operator fun component4() = body

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

            operator fun component4() = packet

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

        operator fun component1() = type
        operator fun component2() = sequenceID
        operator fun component3() = command

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
        ) : Response {

            constructor(other: Buffered) : this(
                type = other.type,
                encryptType = other.encryptType,
                uin = other.uin,
                sequenceID = other.sequenceID,
                command = other.command,
                message = other.message
            ) {
                @Suppress("LeakingThis")
                readBody(other.body)
            }

        }

        open class Buffered(
            type: Type,
            encryptType: EncryptType,
            uin: Long,
            sequenceID: Int,
            command: String,
            message: String,
        ) : Simple(type, encryptType, uin, sequenceID, command, message) {

            lateinit var body: ByteBuf

            operator fun component4() = body

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

            operator fun component4() = packet

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

    enum class EncryptType(val value: UByte) {

        NONE(0x00u), D2_KEY(0x01u), EMPTY_KEY(0x02u);

        companion object {

            fun of(value: UByte) = values().first { it.value == value }

        }

    }

}