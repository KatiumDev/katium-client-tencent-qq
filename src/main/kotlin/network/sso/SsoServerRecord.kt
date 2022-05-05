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
package katium.client.qq.network.sso

import katium.client.qq.network.codec.jce.SimpleJceStruct

class SsoServerRecord(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var address: String by string(1u)
    var port: Int by number(2u)
    var unknown3: Byte by number(3u)
    var unknown4: Byte by number(4u)
    var protocolType: Byte by number(5u) // 0,1: Socket 2,3: HTTP, always 0 in `2` list of the response data
    var unknown6: Int by number(6u)
    var unknown7: Byte by number(7u)
    var location: String by string(8u)
    var unknown9: String by string(9u)

    /**
     * Returned by the server, but the client does not decode it.
     */
    var unknown10: Int by number(10u)

    override fun toString() =
        "SsoServerRecord(address='$address', port=$port, unknown3=$unknown3, unknown4=$unknown4, protocolType=$protocolType, " +
                "unknown6=$unknown6, unknown7=$unknown7, location='$location', unknown9='$unknown9', unknown10=$unknown10)"

}