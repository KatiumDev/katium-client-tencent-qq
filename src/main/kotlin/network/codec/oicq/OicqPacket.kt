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
import katium.client.qq.network.QQClient

class OicqPacket private constructor() {

    interface Packet : AutoCloseable {

        val client: QQClient
        val uin: Int
        val command: Short

        override fun close() {
        }

        operator fun component1() = command

    }

    interface Request : Packet {

        val encryption: EncryptType

        fun writeBody(output: ByteBuf)

        abstract class Simple(
            override val client: QQClient,
            override val uin: Int = client.uin.toInt(),
            override val command: Short,
            override val encryption: EncryptType
        ) : Request

        open class Buffered(
            client: QQClient,
            uin: Int = client.uin.toInt(),
            command: Short,
            encryption: EncryptType,
            val body: ByteBuf
        ) :
            Simple(client, uin, command, encryption) {

            operator fun component2() = body

            override fun writeBody(output: ByteBuf) {
                output.writeBytes(body)
            }

            override fun close() {
                super.close()
                body.release()
            }

        }

    }

    interface Response : Packet {

        fun readBody(input: ByteBuf)

        abstract class Simple(
            override val client: QQClient,
            override val uin: Int,
            override val command: Short
        ) : Response

        open class Buffered(client: QQClient, uin: Int, command: Short) :
            Simple(client, uin, command) {

            lateinit var body: ByteBuf

            operator fun component2() = body

            override fun readBody(input: ByteBuf) {
                body = input.readBytes(input.readableBytes())
            }

            override fun close() {
                super.close()
                body.release()
            }

        }

    }

    enum class EncryptType {

        ECDH, ST

    }

}
