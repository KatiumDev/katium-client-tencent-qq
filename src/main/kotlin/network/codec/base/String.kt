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
package katium.client.qq.network.codec.base

import io.netty.buffer.ByteBuf

fun ByteBuf.writeQQIntLengthString(string: String, long: Boolean): ByteBuf {
    val bytes = string.toByteArray()
    writeInt(bytes.size + (if(long) 4 else 0))
    writeBytes(bytes)
    return this
}

fun ByteBuf.readQQIntLengthString(long: Boolean): String {
    val buffer = ByteArray(readInt() - (if(long) 4 else 0))
    readBytes(buffer)
    return String(buffer)
}

fun ByteBuf.writeQQShortLengthString(string: String): ByteBuf {
    val bytes = string.toByteArray()
    writeShort(bytes.size)
    writeBytes(bytes)
    return this
}

fun ByteBuf.readQQShortLengthString(): String {
    val buffer = ByteArray(readShort().toInt())
    readBytes(buffer)
    return String(buffer)
}
