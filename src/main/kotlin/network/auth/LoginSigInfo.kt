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
    /**
     * Decode with TeaCipher.decodeByteKey
     */
    var d2Key: UIntArray? = UIntArray(4),
    var deviceToken: UByteArray? = null,

    var psKeyMap: Map<String, ByteArray>? = null,
    var pt4TokenMap: Map<String, ByteArray>? = null,

    // others
    val outgoingPacketSessionId: UByteArray = ubyteArrayOf(0x02u, 0xB0u, 0x5Bu, 0x8Bu),
    var dpwd: UByteArray? = null,

    // TLV cache
    /*var t104: UByteArray = UByteArray(0),
    var t174: UByteArray = UByteArray(0),*/
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
    /*var syncCookie: UByteArray = UByteArray(0),
    var publicAccountCookie: UByteArray = UByteArray(0),*/
    var ksid: UByteArray = UByteArray(0),
    //var msgCtrlBuf: UByteArray,
)
