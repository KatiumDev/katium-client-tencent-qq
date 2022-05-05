package katium.client.qq.network.codec.auth

object BuiltinClientVersions {

    @JvmField
    val ANDROID_PHONE = ClientVersionInfo(
        apkID = "com.tencent.mobileqq",
        appID = 537066978,
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

}