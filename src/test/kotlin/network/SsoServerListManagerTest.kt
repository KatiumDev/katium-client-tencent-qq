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

import katium.client.qq.network.sso.SsoServerListManager
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class SsoServerListManagerTest {

    @Test
    fun `fetch sso server list`() {
        runBlocking {
            println("All server records: ${SsoServerListManager.fetchRecords()}")
        }
    }

    @Test
    fun `resolve addresses`() {
        runBlocking {
            println("Resolved server addresses: ${SsoServerListManager.fetchAddresses()}")
        }
    }

    @Test
    fun `sorted addresses`() {
        runBlocking {
            println("Sorted server addresses: ${SsoServerListManager.fetchSortedAddresses()}")
        }
    }

    @Test
    fun `addresses for connection`() {
        runBlocking {
            println("Addresses for connection: ${SsoServerListManager.fetchAddressesForConnection()}")
        }
    }

}
