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
package katium.client.qq.util

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CoroutineLazy<T>(val scope: CoroutineScope, val provider: suspend () -> T) {

    private var value: AtomicRef<T?> = atomic(null)
    private var mutex = Mutex()
    private var waiters: LinkedHashSet<CancellableContinuation<T>>? = null

    suspend fun get(): T {
        if (value.value == null) {
            mutex.lock(this)
            if (value.value == null) {
                if (waiters == null) {
                    waiters = LinkedHashSet()
                    scope.launch {
                        try {
                            val result = provider()
                            mutex.withLock(this@CoroutineLazy) {
                                if (value.value == null) value.value = result
                                waiters!!.forEach { it.resume(result) }
                            }
                        } catch (e: Throwable) {
                            mutex.withLock(this@CoroutineLazy) {
                                waiters!!.forEach { it.resumeWithException(e) }
                            }
                        }
                        waiters = null
                    }
                }
                return suspendCancellableCoroutine {
                    waiters!!.add(it)
                    mutex.unlock(this)
                }
            } else mutex.unlock(this)
        }
        return value.value!!
    }

    fun getSync(): T {
        if (value.value == null) {
            return runBlocking(scope.coroutineContext) {
                return@runBlocking get()
            }
        }
        return value.value!!
    }

    fun set(value: T) {
        this.value.value = value
    }

}