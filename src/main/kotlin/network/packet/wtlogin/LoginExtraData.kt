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
package katium.client.qq.network.packet.wtlogin

import io.netty.buffer.ByteBuf
import katium.core.util.netty.writeUByteArray

data class LoginExtraData(
    val uin: Long,
    val ip: UByteArray,
    val time: Int,
    val version: Int
)

fun ByteBuf.writeLoginExtraData(data: LoginExtraData): ByteBuf {
    writeLong(data.uin)
    writeByte(data.ip.size)
    writeUByteArray(data.ip)
    writeInt(data.time)
    writeInt(data.version)
    return this
}

