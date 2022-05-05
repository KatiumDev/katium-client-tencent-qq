/*
 * Katium Client Tencent QQ: Tencent QQ protocol implementation for Katium
 * Copyright (C) 2022  Katium Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package katium.client.qq.test.network.sso

import katium.client.qq.network.sso.SsoServerListManager
import katium.core.BotPlatform
import katium.core.event.BotOnlineEvent
import katium.core.util.event.EventListener
import katium.core.util.event.Subscribe
import katium.core.util.event.register
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class SsoServerListManager {

    @Test
    fun `Fetch SSO Server List`() {
        /*runBlocking {
            val bot = BotPlatform.lookup.query("qq").createBot(mapOf("qq.user" to "3195210395"))
            bot.register(object : EventListener {

                @Subscribe
                suspend fun online(event: BotOnlineEvent) {
                    println("online")
                }

            })
            bot.startAndJoin()
        }*/
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
