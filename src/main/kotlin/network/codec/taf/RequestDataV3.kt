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
package katium.client.qq.network.codec.taf

import io.netty.buffer.ByteBuf
import katium.client.qq.network.codec.jce.SimpleJceStruct

class RequestDataV3(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var map: MutableMap<String, ByteBuf> by map(0u)

    operator fun get(key: String) = map[key]
    operator fun set(key: String, value: ByteBuf) = map.put(key, value)

    override fun release() {
        super.release()
        map.values.forEach(ByteBuf::release)
    }

    override fun toString() = "RequestDataV3($map)"

}

fun RequestDataV3(vararg pairs: Pair<String, ByteBuf>) = RequestDataV3().apply {
    map.putAll(pairs)
}
