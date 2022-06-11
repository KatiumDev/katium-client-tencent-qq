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
package katium.client.qq.test.network

import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.core.util.netty.heapBuffer
import katium.core.util.netty.toArray
import org.junit.jupiter.api.RepeatedTest
import java.util.*
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class QQTeaCipherTest {

    @Test
    fun decrypt() {
        val cipher0 = QQTeaCipher(0u, 0u, 0u, 0u)
        assertEquals(
            "080318022a0208022a021005320218043a1a4142434445464748494a4b4c4d4e4f505152535455565758595a556677",
            HexFormat.of().formatHex(
                cipher0.decrypt(
                    PooledByteBufAllocator.DEFAULT.heapBuffer(
                        HexFormat.of()
                            .parseHex("7901fcd147d4b7da52318171614517589e6c15811d32dc60e759c802b1e8582bb9a4fea9a419d30eb6f5d99bc3b61759c74af97c9d79e0f7c90e44cf81e2da85")
                    )
                ).toArray(true)
            )
        )
        val cipher1 = QQTeaCipher(0u, 0u, 0u, 1u)
        assertEquals(
            "080318022a0208022a021005320218043a1a4142434445464748494a4b4c4d4e4f505152535455565758595a556677",
            HexFormat.of().formatHex(
                cipher1.decrypt(
                    PooledByteBufAllocator.DEFAULT.heapBuffer(
                        HexFormat.of()
                            .parseHex("d632c7768a25d61543f128983ea909315f3188df63303fe547de8228137f81d62d3a5e597d8dbe715b5b7d8d201106f56e9b389b5a0fc79e1c410aa200e50a79")
                    )
                ).toArray(true)
            )
        )
    }

    @RepeatedTest(100)
    fun fuzzing() {
        val random = Random.Default
        for (i in 0..100) {
            val cipher = QQTeaCipher(random.nextUBytes(16))
            val source = random.nextBytes(random.nextInt(16, 1024))
            val encrypted = cipher.encrypt(PooledByteBufAllocator.DEFAULT.heapBuffer(source)).toArray(true)
            val decrypted = cipher.decrypt(PooledByteBufAllocator.DEFAULT.heapBuffer(encrypted)).toArray(true)
            assertContentEquals(source, decrypted)
        }
    }

}