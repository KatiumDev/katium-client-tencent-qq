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
package katium.client.qq.network.packet.wtlogin

import katium.client.qq.network.QQClient
import katium.client.qq.network.codec.packet.TransportPacket

fun createLoginRequest(client: QQClient, sequenceID: Int = client.allocSequenceID()) = TransportPacket.Request.Oicq(
    client = client,
    type = TransportPacket.Type.LOGIN,
    encryptType = TransportPacket.EncryptType.EMPTY_KEY,
    sequenceID = sequenceID,
    command = "wtlogin.login",
    packet = PasswordLoginPacket(client, sequenceID)
)
