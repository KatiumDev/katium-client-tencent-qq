package katium.client.qq.network.codec.highway

import com.google.common.hash.Hashing

data class HighwayTransaction(
    /**
     * 1: Friend, 2: Group, 299: Group PTT
     */
    val command: Int,
    val ticket: UByteArray,
    val body: UByteArray,
    val extension: UByteArray = ubyteArrayOf(),
    val chunkSize: Int = 1024 * 64
) {

    val bodyMd5: ByteArray by lazy {
        @Suppress("DEPRECATION")
        Hashing.md5().hashBytes(body.toByteArray()).asBytes()
    }

}