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
package katium.client.qq.network.codec.oidb

import io.netty.buffer.ByteBuf
import katium.client.qq.network.QQClient
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
fun ByteBuf.writeOidbPacket(client: QQClient, command: Int, serviceType: Int, body: ByteArray): ByteBuf {
    writeBytes(
        ProtoBuf.encodeToByteArray(
            PbOidbPacket(
                command = command,
                serviceType = serviceType,
                buffer = body,
                clientVersion = "Android ${client.version.version}",
                error = "",
                result = 0
            )
        )
    )
    return this
}

@OptIn(ExperimentalSerializationApi::class)
fun ByteBuf.readOidbPacket(): PbOidbPacket {
    val buffer = ByteArray(readableBytes())
    readBytes(buffer)
    val packet = ProtoBuf.decodeFromByteArray<PbOidbPacket>(buffer)
    if (packet.result != 0) throw IllegalStateException("Oidb packet result=${packet.result}, error=${packet.error}")
    return packet
}
