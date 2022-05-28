package katium.client.qq.message.content

import com.google.protobuf.ByteString
import katium.core.message.content.Image

class QQImage(val resourceKey: String, originUrl: String, val md5: ByteString): Image() {

    override val contentBytes: ByteArray?
        get() = TODO("Not yet implemented")

    override val contentUrl: String = "https://c2cpicdw.qpic.cn/$originUrl"

}