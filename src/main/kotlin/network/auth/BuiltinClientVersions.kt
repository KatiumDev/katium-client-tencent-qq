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

object BuiltinClientVersions {

    @JvmField
    val ANDROID_PHONE = ClientVersionInfo(
        apkID = "com.tencent.mobileqq",
        appID = 537100432,
        subAppID = 537100432,
        version = "8.8.38",
        sdkVersion = "6.0.0.2487",
        miscBitMap = 184024956,
        subSigMap = 0x10400,
        mainSigMap = 34869472,
        signature = "A6B745BF24A2C277527716F6F36EB68D",
        buildTime = 1634310940,
        ssoVersion = 16,
        protocolType = ProtocolType.ANDROID_PHONE,
    )

    @JvmField
    val ANDROID_WATCH = ClientVersionInfo(
        apkID = "com.tencent.qqlite",
        appID = 537064446,
        subAppID = 537064446,
        version = "2.0.5",
        sdkVersion = "6.0.0.236",
        miscBitMap = 16252796,
        subSigMap = 0x10400,
        mainSigMap = 34869472,
        signature = "A6B745BF24A2C277527716F6F36EB68D",
        buildTime = 1559564731,
        ssoVersion = 5,
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
        buildTime = 1595836208,
        ssoVersion = 12,
        protocolType = ProtocolType.MAC_OS,
    )

    @JvmField
    val I_PAD = ClientVersionInfo(
        apkID = "com.tencent.minihd.qq",
        appID = 537097188,
        subAppID = 537097188,
        version = "8.8.35",
        sdkVersion = "6.0.0.2433",
        miscBitMap = 150470524,
        subSigMap = 66560,
        mainSigMap = 1970400,
        signature = "AA3978F41FD96FF9914A669E186474C7",
        buildTime = 1595836208,
        ssoVersion = 12,
        protocolType = ProtocolType.I_PAD,
    )

    val versions = mapOf(
        ProtocolType.ANDROID_PHONE to ANDROID_PHONE,
        ProtocolType.ANDROID_WATCH to ANDROID_WATCH,
        ProtocolType.MAC_OS to MAC_OS,
        ProtocolType.I_PAD to I_PAD,
    )

}