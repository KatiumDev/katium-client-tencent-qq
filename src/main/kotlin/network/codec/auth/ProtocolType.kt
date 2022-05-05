package katium.client.qq.network.codec.auth

enum class ProtocolType(val builtinVersion: ClientVersionInfo) {

    ANDROID_PHONE(BuiltinClientVersions.ANDROID_PHONE),
    ANDROID_PAD(BuiltinClientVersions.ANDROID_PAD),
    ANDROID_WATCH(BuiltinClientVersions.ANDROID_WATCH),
    MAC_OS(BuiltinClientVersions.MAC_OS),
    I_PAD(BuiltinClientVersions.I_PAD),

}