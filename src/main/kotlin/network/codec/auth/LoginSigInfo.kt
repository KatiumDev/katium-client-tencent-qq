package katium.client.qq.network.codec.auth

@Suppress("OPT_IN_USAGE")
data class LoginSigInfo(
    var loginBitmap: ULong = 0uL,
    var tgt: UByteArray? = null,
    var tgtKey: UByteArray = UByteArray(0),

    var studyRoomManagerToken: UByteArray = UByteArray(0), // study room manager | 0x16a
    var t133: UByteArray = UByteArray(0),
    var encryptedA1: UByteArray = UByteArray(0),
    var userStKey: UByteArray = UByteArray(0),
    var userStWebSig: UByteArray = UByteArray(0),
    var sKey: UByteArray = UByteArray(0),
    var sKeyExpiredTime: Long = 0,
    var d2: UByteArray? = null,
    var d2Key: UIntArray = UIntArray(0), // decode with TeaCipher.decodeByteKey
    var deviceToken: UByteArray = UByteArray(0),

    var psKeyMap: Map<String, UByteArray> = mapOf(),
    var pt4TokenMap: Map<String, UByteArray> = mapOf(),

    // others
    val outgoingPacketSessionId: UByteArray = ubyteArrayOf(0x02u, 0xB0u, 0x5Bu, 0x8Bu),
    var dpwd: UByteArray = UByteArray(0),

    // TLV cache
    var t104: UByteArray = UByteArray(0),
    var t174: UByteArray = UByteArray(0),
    var g: UByteArray = UByteArray(0),
    var t402: UByteArray = UByteArray(0),
    var randomSeed: UByteArray = UByteArray(0), // t403
    /*var rollbackSig: UByteArray,
    var t149: UByteArray,
    var t150: UByteArray,
    var t528: UByteArray,
    var t530: UByteArray,*/

    // Sync info
    var syncCookie: UByteArray = UByteArray(0),
    var publicAccountCookie: UByteArray = UByteArray(0),
    var ksid: UByteArray = UByteArray(0),
    //var msgCtrlBuf: UByteArray,
) {
}