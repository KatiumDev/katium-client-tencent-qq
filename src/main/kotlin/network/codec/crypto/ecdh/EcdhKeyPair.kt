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
package katium.client.qq.network.codec.crypto.ecdh

import com.google.common.hash.Hashing
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.Security
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.KeyAgreement


data class EcdhKeyPair(
    val serverKeyVersion: Int,
    val clientPublicKey: PublicKey,
    val shareKey: UByteArray
) {

    val clientPublicKeyEncoded: ByteArray = clientPublicKey.encoded
    val shareKeyTeaCipher = QQTeaCipher(shareKey)

    companion object {

        init {
            Security.addProvider(BouncyCastleProvider())
        }

        @JvmField
        val X509_PREFIX: ByteArray = HexFormat.of().parseHex("3059301306072A8648CE3D020106082A8648CE3D030107034200")

        val Builtin = create(
            1,
            "04EBCA94D733E399B2DB96EACDD3F69A8BB0F74224E2B44E3357812211D2E62EFBC91BB553098E25E33A799ADC7F76FEB208DA7C6522CDB0719A305180CC54A82E"
        )

        fun create(serverKeyVersion: Int, serverPublicKey: String): EcdhKeyPair {
            val serverKey = KeyFactory.getInstance("EC")
                .generatePublic(X509EncodedKeySpec(X509_PREFIX + HexFormat.of().parseHex(serverPublicKey)))
            val clientKeyPair = KeyPairGenerator.getInstance("ECDH")
                .run {
                    initialize(ECGenParameterSpec("prime256v1"))
                    generateKeyPair()
                }
            val shareKey = KeyAgreement.getInstance("ECDH").run {
                init(clientKeyPair.private)
                doPhase(serverKey, true)
                @Suppress("DEPRECATION")
                Hashing.md5().hashBytes(generateSecret().copyOf(16)).asBytes().toUByteArray()
            }
            return EcdhKeyPair(serverKeyVersion, clientKeyPair.public, shareKey)
        }

    }

}
