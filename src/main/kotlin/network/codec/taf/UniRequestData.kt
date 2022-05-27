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
package katium.client.qq.network.codec.taf

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import katium.core.util.netty.buffer

fun ByteBuf.wrapUniRequestData(release: Boolean = true): ByteBuf = ByteBufAllocator.DEFAULT.buffer {
    writeByte(0x0A)
    writeBytes(this@wrapUniRequestData)
    writeByte(0x0B)
    if (release) this@wrapUniRequestData.release()
}

/**
 * Decode a UniRequestData.
 *
 * The reference counter of the ByteBuf is not changed.
 */
fun ByteBuf.unwrapUniRequestData(size : Int = readableBytes() - 2): ByteBuf {
    skipBytes(1)
    val data = readSlice(size)
    skipBytes(1)
    return data
}