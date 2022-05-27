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
package katium.client.qq.network.crypto.ecdh

import com.google.common.hash.Hashing
import katium.client.qq.network.crypto.tea.QQTeaCipher
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

    val clientPublicKeyEncoded: ByteArray = clientPublicKey.encoded.copyOfRange(26, 91)
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
