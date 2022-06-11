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

class CoroutineLazy<T>(val scope: CoroutineScope, provider: (suspend () -> T)) {

    private var provider: (suspend () -> T)? = provider
    private var value: AtomicRef<T?> = atomic(null)
    private var mutex: Mutex? = null
    private var waiters: LinkedHashSet<CancellableContinuation<T>>? = null

    suspend fun get(): T {
        if (provider != null && value.value == null) {
            synchronized(this) {
                if (mutex == null) mutex = Mutex()
            }
            mutex!!.lock(this)
            if (value.value == null) {
                if (waiters == null) {
                    waiters = LinkedHashSet()
                    scope.launch {
                        try {
                            val result = provider!!()
                            mutex!!.withLock(this@CoroutineLazy) {
                                if (value.value == null) value.value = result
                                waiters!!.forEach { it.resume(result) }
                            }
                        } catch (e: Throwable) {
                            mutex!!.withLock(this@CoroutineLazy) {
                                waiters!!.forEach { it.resumeWithException(e) }
                            }
                        }
                        mutex = null
                        provider = null
                        waiters = null
                    }
                }
                return suspendCancellableCoroutine {
                    waiters!!.add(it)
                    mutex!!.unlock(this)
                }
            } else mutex!!.unlock(this)
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