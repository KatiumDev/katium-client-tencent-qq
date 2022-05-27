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
package katium.client.qq.network.codec.tlv

import io.netty.buffer.ByteBuf
import katium.client.qq.network.codec.base.readQQShortLengthString

/**
 * @return (psKeyMap, pt4TokenMap)
 */
fun ByteBuf.readT512(release: Boolean = true): Pair<Map<String, ByteArray>, Map<String, ByteArray>> {
    val length = readShort()
    val psKeyMap = mutableMapOf<String, ByteArray>()
    val pt4TokenMap = mutableMapOf<String, ByteArray>()
    for (i in 0 until length) {
        val domain = readQQShortLengthString()
        val psKey = readQQShortLengthString().toByteArray()
        val pt4Token = readQQShortLengthString().toByteArray()
        if (psKey.isNotEmpty()) {
            psKeyMap[domain] = psKey
        }
        if (pt4Token.isNotEmpty()) {
            pt4TokenMap[domain] = pt4Token
        }
    }
    if (release) {
        release()
    }
    return psKeyMap.toMap() to pt4TokenMap.toMap()
}