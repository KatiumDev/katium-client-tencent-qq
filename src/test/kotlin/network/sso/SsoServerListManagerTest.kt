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
package katium.client.qq.test.network.sso

import katium.client.qq.network.sso.SsoServerListManager
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class SsoServerListManagerTest {

    @Test
    fun `Fetch SSO Server List`() {
        runBlocking {
            println("All server records: ${SsoServerListManager.fetchRecords()}")
        }
    }

    @Test
    fun `Resolve Server Addresses`() {
        runBlocking {
            println("Resolved server addresses: ${SsoServerListManager.fetchAddresses()}")
        }
    }

    @Test
    fun `Sorted Server Addresses`() {
        runBlocking {
            println("Sorted server addresses: ${SsoServerListManager.fetchSortedAddresses()}")
        }
    }

    @Test
    fun `Addresses for Connection`() {
        runBlocking {
            println("Addresses for connection: ${SsoServerListManager.fetchAddressesForConnection()}")
        }
    }

}
