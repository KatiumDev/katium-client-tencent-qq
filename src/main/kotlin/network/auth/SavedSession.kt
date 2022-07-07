package katium.client.qq.network.auth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
// @TODO: saving ECDH v2
data class SavedSession(
    @ProtoNumber(1) val uin: Long,
    @ProtoNumber(2) val d2: ByteArray,
    @ProtoNumber(3) val d2Key: ByteArray,
    @ProtoNumber(4) val tgt: ByteArray,
    @ProtoNumber(5) val tgtgtKey: ByteArray,
    @ProtoNumber(6) val t133: ByteArray? = null,
    @ProtoNumber(7) val encryptedA1: ByteArray? = null,
    @ProtoNumber(8) val wtSessionTicketKey: ByteArray? = null,
    @ProtoNumber(9) val srmToken: ByteArray? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SavedSession) return false

        if (uin != other.uin) return false
        if (!d2.contentEquals(other.d2)) return false
        if (!d2Key.contentEquals(other.d2Key)) return false
        if (!tgt.contentEquals(other.tgt)) return false
        if (!tgtgtKey.contentEquals(other.tgtgtKey)) return false
        if (t133 != null) {
            if (other.t133 == null) return false
            if (!t133.contentEquals(other.t133)) return false
        } else if (other.t133 != null) return false
        if (encryptedA1 != null) {
            if (other.encryptedA1 == null) return false
            if (!encryptedA1.contentEquals(other.encryptedA1)) return false
        } else if (other.encryptedA1 != null) return false
        if (wtSessionTicketKey != null) {
            if (other.wtSessionTicketKey == null) return false
            if (!wtSessionTicketKey.contentEquals(other.wtSessionTicketKey)) return false
        } else if (other.wtSessionTicketKey != null) return false
        if (srmToken != null) {
            if (other.srmToken == null) return false
            if (!srmToken.contentEquals(other.srmToken)) return false
        } else if (other.srmToken != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uin.hashCode()
        result = 31 * result + d2.contentHashCode()
        result = 31 * result + d2Key.contentHashCode()
        result = 31 * result + tgt.contentHashCode()
        result = 31 * result + tgtgtKey.contentHashCode()
        result = 31 * result + (t133?.contentHashCode() ?: 0)
        result = 31 * result + (encryptedA1?.contentHashCode() ?: 0)
        result = 31 * result + (wtSessionTicketKey?.contentHashCode() ?: 0)
        result = 31 * result + (srmToken?.contentHashCode() ?: 0)
        return result
    }

}
