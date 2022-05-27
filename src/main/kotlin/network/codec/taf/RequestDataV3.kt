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
