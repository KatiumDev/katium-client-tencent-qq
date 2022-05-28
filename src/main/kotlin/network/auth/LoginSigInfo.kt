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
package katium.client.qq.network.auth

data class LoginSigInfo(
    var loginBitmap: ULong = 0uL,
    var tgt: UByteArray = UByteArray(0),
    var tgtKey: UByteArray = UByteArray(0),

    /**
     * study room manager | 0x16a
     */
    var srmToken: UByteArray? = null,
    var t133: UByteArray? = null,
    var encryptedA1: UByteArray? = null,
    var userStKey: UByteArray? = null,
    var userStWebSig: UByteArray? = null,
    var sKey: UByteArray? = null,
    var sKeyExpiredTime: Long = 0,
    var d2: UByteArray = UByteArray(0),
    var d2KeyEncoded: UByteArray = UByteArray(16),
    /**
     * Decode with TeaCipher.decodeByteKey
     */
    var d2Key: UIntArray = UIntArray(4),
    var deviceToken: UByteArray? = null,

    var psKeyMap: Map<String, ByteArray>? = null,
    var pt4TokenMap: Map<String, ByteArray>? = null,

    // others
    val outgoingPacketSessionId: UByteArray = ubyteArrayOf(0x02u, 0xB0u, 0x5Bu, 0x8Bu),
    var dpwd: UByteArray? = null,

    // TLV cache
    var t104: UByteArray = UByteArray(0), // SMS verify
    var t174: UByteArray = UByteArray(0), // SMS verify
    var g: UByteArray? = null,
    var t402: UByteArray? = null,
    /**
     * TLV 403
     */
    var randomSeed: UByteArray? = null,
    /*var rollbackSig: UByteArray? = null,
    var t149: UByteArray? = null,
    var t150: UByteArray? = null,
    var t528: UByteArray? = null,
    var t530: UByteArray? = null,*/

    // Sync info
    var ksid: UByteArray = UByteArray(0),
)
