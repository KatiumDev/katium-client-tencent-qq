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
package katium.client.qq.message.content

import katium.core.message.content.Image
import katium.core.util.okhttp.GlobalHttpClient
import katium.core.util.okhttp.await
import katium.core.util.okhttp.expected
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import java.util.*

class QQImage(
    val resourceKey: String,
    override val contentUrl: String,
    val md5: ByteArray,
    val size: Int? = null,
    val filePath: String = "${HexFormat.of().formatHex(md5).uppercase()}.jpg",
    width: Int? = null,
    height: Int? = null,
) : Image(width, height) {

    override val contentBytes: ByteArray by lazy {
        runBlocking(CoroutineName("Download QQ Chat Image")) {
            GlobalHttpClient.newCall(
                Request.Builder()
                    .url(this@QQImage.contentUrl)
                    .get()
                    .build()
            )
                .await()
                .expected(200)
                .body
                .bytes()
        }
    }

    override fun toString() = "[QQImage($resourceKey, $contentUrl)]"

}