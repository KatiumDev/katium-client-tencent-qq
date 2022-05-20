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

object BuiltinClientVersions {

    @JvmField
    val ANDROID_PHONE = ClientVersionInfo(
        apkID = "com.tencent.mobileqq",
        appID = 537066978,
        subAppID = 537066978,
        version = "8.5.5",
        sdkVersion = "6.0.0.2463",
        miscBitMap = 150470524,
        subSigMap = 0x10400,
        mainSigMap = 16724722,
        signature = "A6B745BF24A2C277527716F6F36EB68D",
        buildTime = 1609322643L,
        ssoVersion = 15,
        protocolType = ProtocolType.ANDROID_PHONE,
    )

    @JvmField
    val ANDROID_PAD = ClientVersionInfo(
        apkID = "com.tencent.mobileqq",
        appID = 537062409,
        subAppID = 537062409,
        version = "8.4.18",
        sdkVersion = "6.0.0.2454",
        miscBitMap = 184024956,
        subSigMap = 0x10400,
        mainSigMap = 34869472,
        signature = "A6B745BF24A2C277527716F6F36EB68D",
        buildTime = 1604580615L,
        ssoVersion = 15,
        protocolType = ProtocolType.ANDROID_PAD,
    )

    @JvmField
    val ANDROID_WATCH = ClientVersionInfo(
        apkID = "com.tencent.mobileqq",
        appID = 537061176,
        subAppID = 537061176,
        version = "8.2.7",
        sdkVersion = "6.0.0.2413",
        miscBitMap = 184024956,
        subSigMap = 0x10400,
        mainSigMap = 34869472,
        signature = "A6B745BF24A2C277527716F6F36EB68D",
        buildTime = 1571193922L,
        ssoVersion = 15,
        protocolType = ProtocolType.ANDROID_WATCH,
    )

    @JvmField
    val MAC_OS = ClientVersionInfo(
        apkID = "com.tencent.minihd.qq",
        appID = 537064315,
        subAppID = 537064315,
        version = "5.8.9",
        sdkVersion = "6.0.0.2433",
        miscBitMap = 150470524,
        subSigMap = 66560,
        mainSigMap = 1970400,
        signature = "AA3978F41FD96FF9914A669E186474C7",
        buildTime = 1595836208L,
        ssoVersion = 12,
        protocolType = ProtocolType.MAC_OS,
    )

    @JvmField
    val I_PAD = ClientVersionInfo(
        apkID = "com.tencent.minihd.qq",
        appID = 537065739,
        subAppID = 537065739,
        version = "5.8.9",
        sdkVersion = "6.0.0.2433",
        miscBitMap = 150470524,
        subSigMap = 66560,
        mainSigMap = 1970400,
        signature = "AA3978F41FD96FF9914A669E186474C7",
        buildTime = 1595836208L,
        ssoVersion = 12,
        protocolType = ProtocolType.I_PAD,
    )

    val versions = mapOf(
        ProtocolType.ANDROID_PHONE to ANDROID_PHONE,
        ProtocolType.ANDROID_PAD to ANDROID_PAD,
        ProtocolType.ANDROID_WATCH to ANDROID_WATCH,
        ProtocolType.MAC_OS to MAC_OS,
        ProtocolType.I_PAD to I_PAD,
    )

}