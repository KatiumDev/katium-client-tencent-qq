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
