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

import kotlinx.serialization.Serializable

@Serializable
data class ClientVersionInfo(
    val apkID: String,
    val appID: Int,
    val subAppID: Int,
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
        if (subAppID != other.subAppID) return false
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
        result = 31 * result + subAppID.hashCode()
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
