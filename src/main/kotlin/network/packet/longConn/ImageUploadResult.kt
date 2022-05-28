package katium.client.qq.network.packet.longConn

import com.google.protobuf.ByteString
import java.net.InetSocketAddress

data class ImageUploadResult(
    val message: String? = null,
    val isExists: Boolean = false,
    val resourceKey: String? = null,
    val uploadServers: Collection<InetSocketAddress> = emptyList(),
    val uploadKey: ByteString? = null
)