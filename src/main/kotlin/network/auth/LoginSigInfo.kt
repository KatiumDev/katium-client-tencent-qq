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

import java.util.*

data class LoginSigInfo(
    var loginBitmap: ULong = 0uL,
    var tgt: ByteArray = ByteArray(0),
    var tgtKey: ByteArray = ByteArray(0),

    /**
     * study room manager | 0x16a
     */
    var srmToken: ByteArray? = null,
    var t133: ByteArray? = null,
    var encryptedA1: ByteArray? = null,
    var userStKey: ByteArray? = null,
    var userStWebSig: ByteArray? = null,
    var sKey: ByteArray? = null,
    var sKeyExpiredTime: Long = 0,
    var d2: ByteArray = ByteArray(0),
    var d2KeyEncoded: UByteArray = UByteArray(16),
    /**
     * Decode with TeaCipher.decodeByteKey
     */
    var d2Key: UIntArray = UIntArray(4),
    var deviceToken: ByteArray? = null,

    var psKeyMap: Map<String, ByteArray>? = null,
    var pt4TokenMap: Map<String, ByteArray>? = null,

    // others
    val outgoingPacketSessionId: ByteArray =HexFormat.of().parseHex("02B05B8B"),
    var dpwd: ByteArray? = null,

    // TLV cache
    var t104: ByteArray = ByteArray(0), // SMS verify
    var t174: ByteArray = ByteArray(0), // SMS verify
    var g: ByteArray? = null,
    var t402: ByteArray? = null,
    /**
     * TLV 403
     */
    var randomSeed: ByteArray? = null,
    /*var rollbackSig: ByteArray? = null,
    var t149: ByteArray? = null,
    var t150: ByteArray? = null,
    var t528: ByteArray? = null,
    var t530: ByteArray? = null,*/

    // Sync info
    var ksid: ByteArray = ByteArray(0),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoginSigInfo) return false

        if (loginBitmap != other.loginBitmap) return false
        if (!tgt.contentEquals(other.tgt)) return false
        if (!tgtKey.contentEquals(other.tgtKey)) return false
        if (srmToken != null) {
            if (other.srmToken == null) return false
            if (!srmToken.contentEquals(other.srmToken)) return false
        } else if (other.srmToken != null) return false
        if (t133 != null) {
            if (other.t133 == null) return false
            if (!t133.contentEquals(other.t133)) return false
        } else if (other.t133 != null) return false
        if (encryptedA1 != null) {
            if (other.encryptedA1 == null) return false
            if (!encryptedA1.contentEquals(other.encryptedA1)) return false
        } else if (other.encryptedA1 != null) return false
        if (userStKey != null) {
            if (other.userStKey == null) return false
            if (!userStKey.contentEquals(other.userStKey)) return false
        } else if (other.userStKey != null) return false
        if (userStWebSig != null) {
            if (other.userStWebSig == null) return false
            if (!userStWebSig.contentEquals(other.userStWebSig)) return false
        } else if (other.userStWebSig != null) return false
        if (sKey != null) {
            if (other.sKey == null) return false
            if (!sKey.contentEquals(other.sKey)) return false
        } else if (other.sKey != null) return false
        if (sKeyExpiredTime != other.sKeyExpiredTime) return false
        if (!d2.contentEquals(other.d2)) return false
        if (d2KeyEncoded != other.d2KeyEncoded) return false
        if (d2Key != other.d2Key) return false
        if (deviceToken != null) {
            if (other.deviceToken == null) return false
            if (!deviceToken.contentEquals(other.deviceToken)) return false
        } else if (other.deviceToken != null) return false
        if (psKeyMap != other.psKeyMap) return false
        if (pt4TokenMap != other.pt4TokenMap) return false
        if (!outgoingPacketSessionId.contentEquals(other.outgoingPacketSessionId)) return false
        if (dpwd != null) {
            if (other.dpwd == null) return false
            if (!dpwd.contentEquals(other.dpwd)) return false
        } else if (other.dpwd != null) return false
        if (!t104.contentEquals(other.t104)) return false
        if (!t174.contentEquals(other.t174)) return false
        if (g != null) {
            if (other.g == null) return false
            if (!g.contentEquals(other.g)) return false
        } else if (other.g != null) return false
        if (t402 != null) {
            if (other.t402 == null) return false
            if (!t402.contentEquals(other.t402)) return false
        } else if (other.t402 != null) return false
        if (randomSeed != null) {
            if (other.randomSeed == null) return false
            if (!randomSeed.contentEquals(other.randomSeed)) return false
        } else if (other.randomSeed != null) return false
        if (!ksid.contentEquals(other.ksid)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loginBitmap.hashCode()
        result = 31 * result + tgt.contentHashCode()
        result = 31 * result + tgtKey.contentHashCode()
        result = 31 * result + (srmToken?.contentHashCode() ?: 0)
        result = 31 * result + (t133?.contentHashCode() ?: 0)
        result = 31 * result + (encryptedA1?.contentHashCode() ?: 0)
        result = 31 * result + (userStKey?.contentHashCode() ?: 0)
        result = 31 * result + (userStWebSig?.contentHashCode() ?: 0)
        result = 31 * result + (sKey?.contentHashCode() ?: 0)
        result = 31 * result + sKeyExpiredTime.hashCode()
        result = 31 * result + d2.contentHashCode()
        result = 31 * result + d2KeyEncoded.hashCode()
        result = 31 * result + d2Key.hashCode()
        result = 31 * result + (deviceToken?.contentHashCode() ?: 0)
        result = 31 * result + (psKeyMap?.hashCode() ?: 0)
        result = 31 * result + (pt4TokenMap?.hashCode() ?: 0)
        result = 31 * result + outgoingPacketSessionId.contentHashCode()
        result = 31 * result + (dpwd?.contentHashCode() ?: 0)
        result = 31 * result + t104.contentHashCode()
        result = 31 * result + t174.contentHashCode()
        result = 31 * result + (g?.contentHashCode() ?: 0)
        result = 31 * result + (t402?.contentHashCode() ?: 0)
        result = 31 * result + (randomSeed?.contentHashCode() ?: 0)
        result = 31 * result + ksid.contentHashCode()
        return result
    }

}
