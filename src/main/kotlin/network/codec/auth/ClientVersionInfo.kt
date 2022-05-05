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
package katium.client.qq.network.codec.auth

import kotlinx.serialization.Serializable

@Serializable
data class ClientVersionInfo(
    val apkID: String,
    val appID: Long,
    val version: String,
    val sdkVersion: String,
    val miscBitMap: Int,
    val subSigMap: Int,
    val mainSigMap: Int,
    val signature: String,
    val buildTime: Long,
    val ssoVersion: Int,
    val protocolType: ProtocolType
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClientVersionInfo) return false
        if (apkID != other.apkID) return false
        if (appID != other.appID) return false
        if (version != other.version) return false
        if (sdkVersion != other.sdkVersion) return false
        if (miscBitMap != other.miscBitMap) return false
        if (subSigMap != other.subSigMap) return false
        if (mainSigMap != other.mainSigMap) return false
        if (signature != other.signature) return false
        if (buildTime != other.buildTime) return false
        if (ssoVersion != other.ssoVersion) return false
        if (protocolType != other.protocolType) return false
        return true
    }

    override fun hashCode(): Int {
        var result = apkID.hashCode()
        result = 31 * result + appID.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + sdkVersion.hashCode()
        result = 31 * result + miscBitMap
        result = 31 * result + subSigMap
        result = 31 * result + mainSigMap
        result = 31 * result + signature.hashCode()
        result = 31 * result + buildTime.hashCode()
        result = 31 * result + ssoVersion
        result = 31 * result + protocolType.hashCode()
        return result
    }

}
