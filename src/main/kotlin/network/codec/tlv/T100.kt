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

import io.netty.buffer.ByteBuf

fun ByteBuf.writeT100(
    appID: Int = 16,
    subAppID: Int,
    appClientVersion: Int = 0,
    ssoVersion: Int,
    mainSigMap: Int
) = writeTlv(0x100) {
    writeShort(1) // dbBufferVersion
    writeInt(ssoVersion)
    writeInt(appID)
    writeInt(subAppID)
    writeInt(appClientVersion)
    writeInt(mainSigMap)
}
