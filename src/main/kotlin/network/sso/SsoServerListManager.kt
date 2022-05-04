package katium.client.qq.network.sso

import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.codec.crypto.QQTeaCipher
import katium.client.qq.network.codec.jce.*
import katium.client.qq.network.codec.struct.*
import katium.core.util.netty.buffer
import katium.core.util.okhttp.GlobalHttpClient
import katium.core.util.okhttp.await
import katium.core.util.okhttp.expected
import katium.core.util.okhttp.toRequestBody
import okhttp3.Request

object SsoServerListManager {

    const val SERVER_URL = "https://configsvr.msf.3g.qq.com/configsvr/serverlist.jsp?mType=getssolist"

    /**
     * Key: F0441F5FF42DA58FDCF7949ABA62D411(aka as 0xF0441F5Fu, 0xF42DA58Fu, 0xDCF7949Au, 0xBA62D411u)
     */
    @JvmField
    val CIPHER = QQTeaCipher(0xF0441F5Fu, 0xF42DA58Fu, 0xDCF7949Au, 0xBA62D411u)

    suspend fun fetch() {
        val payload = CIPHER.encrypt(ByteBufAllocator.DEFAULT.buffer {
            writeIntLvPacket {
                writeBytes(
                    RequestPacket(
                        version = 3,
                        servantName = "ConfigHttp",
                        functionName = "HttpServerListReq",
                        buffer = RequestDataV3(
                            "HttpServerListReq" to ByteBufAllocator.DEFAULT.buffer {

                            }.wrapUniRequestData()
                        ).dump()
                    ).dump()
                )
            }
        }).toRequestBody(true)
        val response = GlobalHttpClient.newCall(
            Request.Builder()
                .url(SERVER_URL)
                .post(payload)
                .build()
        ).await().expected(200)
        RequestPacket(
            CIPHER.decrypt(ByteBufAllocator.DEFAULT.buffer(response.body.bytes()))
                .skipBytes(4)
                .readJceStruct()
        ).run {
            RequestDataV3(this.buffer.readJceStruct()).run {
                @Suppress("UNCHECKED_CAST")
                val servers = (this.map["HttpServerListRes"]!!.decodeUniRequestData()
                    .readJceStruct()[2u] as Collection<SimpleJceStruct>)
                    .map(::SsoServerRecord)
                println(servers)
            }
        }
    }

}