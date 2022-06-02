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
package katium.client.qq.network.codec.highway

import com.google.common.hash.Hashing

data class HighwayTransaction(
    /**
     * 1: Friend, 2: Group, 299: Group PTT
     */
    val command: Int,
    val ticket: UByteArray,
    val body: UByteArray,
    val extension: UByteArray = ubyteArrayOf(),
    val chunkSize: Int = 1024 * 64
) {

    val bodyMd5: ByteArray by lazy {
        @Suppress("DEPRECATION")
        Hashing.md5().hashBytes(body.toByteArray()).asBytes()
    }

}