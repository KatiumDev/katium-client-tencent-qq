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

import katium.client.qq.network.QQClient
import katium.core.util.okhttp.GlobalHttpClient
import katium.core.util.okhttp.await
import katium.core.util.okhttp.expected
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Request
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.*

class EcdhKeyProvider(val client: QQClient) {

    companion object {

        const val CIPHER_SUITE_VERSION = 305

        val verifyPublicKey: PublicKey by lazy {
            KeyFactory.getInstance("RSA").generatePublic(
                X509EncodedKeySpec(
                    Base64.getDecoder().decode(
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuJTW4abQJXeVdAODw1" +
                                "CamZH4QJZChyT08ribet1Gp0wpSabIgyKFZAOxeArcCbknKyBrRY3FFI9HgY1AyItH8DOUe6ajDEb6c+vrgjgeC" +
                                "iOiCVyum4lI5Fmp38iHKH14xap6xGaXcBccdOZNzGT82sPDM2Oc6QYSZpfs8EO7TYT7KSB2gaHz99RQ4A/Lel1V" +
                                "w0krk+DescN6TgRCaXjSGn268jD7lOO23x5JS1mavsUJtOZpXkK9GqCGSTCTbCwZhI33CpwdQ2EHLhiP5RaXZCi" +
                                "o6lksu+d8sKTWU1eEiEb3cQ7nuZXLYH7leeYFoPtbFV4RicIWp0/YG+RP7rLPCwIDAQAB"
                    )
                )
            )
        }

        fun verifyKeySignature(keyVersion: Int, key: String, keySignature: String): Boolean =
            Signature.getInstance("SHA256WithRSA").run {
                initVerify(verifyPublicKey)
                update((CIPHER_SUITE_VERSION.toString() + keyVersion.toString() + key).toByteArray())
                return verify(Base64.getDecoder().decode(keySignature))
            }

    }

    var keyPair = EcdhKeyPair.Builtin
    val serverKeyVersion get() = keyPair.serverKeyVersion
    val clientPublicKey get() = keyPair.clientPublicKey
    val shareKey get() = keyPair.shareKey
    val clientPublicKeyEncoded get() = keyPair.clientPublicKeyEncoded
    val shareKeyTeaCipher get() = keyPair.shareKeyTeaCipher
    val oicqSessionCount = atomic(0)

    init {
        if (client.bot.options.ecdhV2Enabled) {
            client.launch(CoroutineName("Fetch ECDH v2")) {
                updateFromKeyRotate()
            }
        }
    }

    suspend fun updateFromKeyRotate() {
        val key = fetchFromKeyRotate()
        client.logger.info("ECDH key v2 got")
        oicqSessionCount.loop {
            if (it == 0) {
                client.logger.info("ECDH key v2 installed")
                keyPair = key
                return
            }
        }
    }

    suspend fun fetchFromKeyRotate(): EcdhKeyPair {
        client.logger.info("Fetching ECDH v2 from server for ${client.uin}...")
        val response: KeyRotateResponse = Json.decodeFromString(
            KeyRotateResponse.serializer(), GlobalHttpClient.newCall(
                Request.Builder()
                    .url("https://keyrotate.qq.com/rotate_key?cipher_suite_ver=$CIPHER_SUITE_VERSION&uin=${client.uin}")
                    .get()
                    .build()
            ).await().expected(200).body.byteStream().reader().readText()
        )
        client.logger.info("Server ECDH public key: ${response.meta.publicKey}")
        if (client.bot.options.ecdhV2Verifying
            && !verifyKeySignature(response.meta.keyVersion, response.meta.publicKey, response.meta.publicKeySignature)
        ) {
            throw IllegalStateException("Invalid key signature")
        } else {
            client.logger.info("ECDH v2 server public key signature verified")
        }
        return EcdhKeyPair.create(response.meta.keyVersion, response.meta.publicKey)
    }

    @Serializable
    internal data class KeyRotateResponse(
        /**
         * Valid time in seconds
         */
        @SerialName("QuerySpan") val querySpan: Int,
        @SerialName("PubKeyMeta") val meta: Meta,
    ) {

        @Serializable
        data class Meta(
            @SerialName("KeyVer") val keyVersion: Int,
            @SerialName("PubKey") val publicKey: String,
            @SerialName("PubKeySign") val publicKeySignature: String,
        )

    }

}
