package katium.client.qq.message.content

import com.google.protobuf.ByteString
import katium.core.message.content.Image
import katium.core.util.okhttp.GlobalHttpClient
import katium.core.util.okhttp.await
import katium.core.util.okhttp.expected
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking
import okhttp3.Request

class QQImage(val resourceKey: String, originUrl: String, val md5: ByteString, val size: Int? = null) : Image() {

    override val contentBytes: ByteArray by lazy {
        runBlocking(CoroutineName("Download QQ Chat Image")) {
            GlobalHttpClient.newCall(
                Request.Builder()
                    .url(contentUrl)
                    .get()
                    .build()
            )
                .await()
                .expected(200)
                .body
                .bytes()
        }
    }

    override val contentUrl = "https://c2cpicdw.qpic.cn/$originUrl"

}