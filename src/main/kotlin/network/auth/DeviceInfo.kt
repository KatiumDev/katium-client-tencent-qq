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

import com.google.common.hash.Hashing
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.random.Random

// @TODO: random generator
@Serializable
data class DeviceInfo(
    val display: String = "GMC.5456781.001",
    val product: String = "iarim",
    val device: String = "sagit",
    val board: String = "eomam",
    val model: String = "MI 6",
    val fingerprint: String = "xiaomi/iarim/sagit:10/eomam.200122.001/2365489:user/release-keys",
    @SerialName("boot_id") val bootID: String = "ee19fb60-808d-46ff-9164-292fac4d2f63",
    @SerialName("proc_version") val procVersion: String = "Linux 5.4.0-54-generic-d5ea85b4 (android-build@google.com)",
    /**
     * 0: Pad 1: Phone 2: Watch
     */
    val protocol: Int = 0,
    val IMEI: String = "352244079486443",
    val brand: String = "Xiaomi",
    val bootloader: String = "unknown",
    @SerialName("base_band") val baseBand: String = "",
    val version: OSVersionInfo = OSVersionInfo(),
    @SerialName("sim_info") val simInfo: String = "T-Mobile",
    @SerialName("os_type") val osType: String = "android",
    @SerialName("mac_address") val macAddress: String = "00:50:56:C0:00:08",
    @SerialName("ip_address") val ipAddress: IntArray = intArrayOf(10, 0, 1, 3),
    @SerialName("wifi_bssid") val wifiBSSID: String = "00:50:56:C0:00:08",
    @SerialName("wifi_ssid") val wifiSSID: String = "<unknown ssid>",
    @SerialName("imsi_md5") val IMSIMD5: String = "c28d938d7b0cdcf681cdb24acb4859fd",
    @SerialName("android_id") val androidID: String = "7b0cdcf681cdb24a",
    val apn: String = "wifi",
    @SerialName("vendor_name") val vendorName: String = "MIUI",
    @SerialName("vendor_os_name") val vendorOSName: String = "gmc",
) {

    @Suppress("DEPRECATION")
    val guid: ByteArray = Hashing.md5().hashBytes((androidID + macAddress).toByteArray()).asBytes()

    @Suppress("DEPRECATION")
    var tgtgtKey: ByteArray = Hashing.md5().hashBytes(Random.Default.nextBytes(16) + guid).asBytes()

    fun computeKsid() = "|${IMEI}|A8.2.7.27f6ea96".toByteArray()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeviceInfo) return false
        if (display != other.display) return false
        if (product != other.product) return false
        if (device != other.device) return false
        if (board != other.board) return false
        if (model != other.model) return false
        if (fingerprint != other.fingerprint) return false
        if (bootID != other.bootID) return false
        if (procVersion != other.procVersion) return false
        if (protocol != other.protocol) return false
        if (IMEI != other.IMEI) return false
        if (brand != other.brand) return false
        if (bootloader != other.bootloader) return false
        if (baseBand != other.baseBand) return false
        if (version != other.version) return false
        if (simInfo != other.simInfo) return false
        if (osType != other.osType) return false
        if (macAddress != other.macAddress) return false
        if (!ipAddress.contentEquals(other.ipAddress)) return false
        if (wifiBSSID != other.wifiBSSID) return false
        if (wifiSSID != other.wifiSSID) return false
        if (IMSIMD5 != other.IMSIMD5) return false
        if (androidID != other.androidID) return false
        if (apn != other.apn) return false
        if (vendorName != other.vendorName) return false
        if (vendorOSName != other.vendorOSName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = display.hashCode()
        result = 31 * result + product.hashCode()
        result = 31 * result + device.hashCode()
        result = 31 * result + board.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + fingerprint.hashCode()
        result = 31 * result + bootID.hashCode()
        result = 31 * result + procVersion.hashCode()
        result = 31 * result + protocol
        result = 31 * result + IMEI.hashCode()
        result = 31 * result + brand.hashCode()
        result = 31 * result + bootloader.hashCode()
        result = 31 * result + baseBand.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + simInfo.hashCode()
        result = 31 * result + osType.hashCode()
        result = 31 * result + macAddress.hashCode()
        result = 31 * result + ipAddress.contentHashCode()
        result = 31 * result + wifiBSSID.hashCode()
        result = 31 * result + wifiSSID.hashCode()
        result = 31 * result + IMSIMD5.hashCode()
        result = 31 * result + androidID.hashCode()
        result = 31 * result + apn.hashCode()
        result = 31 * result + vendorName.hashCode()
        result = 31 * result + vendorOSName.hashCode()
        return result
    }

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class ProtoBuf(
        @ProtoNumber(1) val bootloader: String,
        @ProtoNumber(2) val procVersion: String,
        @ProtoNumber(3) val codeName: String,
        @ProtoNumber(4) val incremental: String,
        @ProtoNumber(5) val fingerprint: String,
        @ProtoNumber(6) val bootID: String,
        @ProtoNumber(7) val androidID: String,
        @ProtoNumber(8) val baseBand: String,
        @ProtoNumber(9) val innerVersion: String,
    ) {

        constructor(info: DeviceInfo) : this(
            bootloader = info.bootloader,
            procVersion = info.procVersion,
            codeName = info.version.codeName,
            incremental = info.version.incremental,
            fingerprint = info.fingerprint,
            bootID = info.bootID,
            androidID = info.androidID,
            baseBand = info.baseBand,
            innerVersion = info.version.incremental,
        )

    }

}