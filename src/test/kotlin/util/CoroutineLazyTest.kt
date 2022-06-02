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
package katium.client.qq.test.util

import katium.client.qq.util.CoroutineLazy
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class CoroutineLazyTest {

    @Test
    fun `Test Coroutine Lazy`() {
        runBlocking {
            coroutineScope {
                val lazy = CoroutineLazy(this) {
                    delay(50)
                }
                for (i in 0..1000) {
                    launch {
                        lazy.get()
                    }
                }
            }
        }
    }

}