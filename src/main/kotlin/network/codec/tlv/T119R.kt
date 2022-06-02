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
package katium.client.qq.network.codec.tlv

import katium.client.qq.network.QQClient
import katium.core.util.netty.toArray

fun TlvMap.applyT119R(client: QQClient) {
    if (0x120 in this) {
        client.sig.sKey = this[0x120]!!.toArray(false).toUByteArray()
        client.sig.sKeyExpiredTime = System.currentTimeMillis() + 21600
    }
    if (0x11A in this) {
        val (age, gender, nick) = this[0x11A]!!.readT11A(false)
    }
}