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
package katium.client.qq.network.event

import katium.client.qq.QQBot
import katium.client.qq.network.QQClient
import katium.core.event.BotEvent

class QQChannelInitializeEvent(val client: QQClient) : BotEvent(client.bot) {

    fun component2() = client

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QQChannelInitializeEvent) return false
        if (!super.equals(other)) return false
        if (client != other.client) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + client.hashCode()
        return result
    }

    override fun toString() = "QQChannelInitializeEvent(bot=$bot, client$client)"

}