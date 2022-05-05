package katium.client.qq.network.codec.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://github.com/lz1998/rs-qq/blob/master/rq-engine/src/protocol/device.rs
 * https://github.com/Mrs4s/MiraiGo/blob/master/client/internal/auth/device.go
 */
@Serializable
data class OSVersionInfo(
    val incremental: String = "5891938",
    val release: String = "10",
    @SerialName("codename") val codeName: String = "REL",
    val sdk: UInt = 29u,
)
