package katium.client.qq.network.sso

import com.google.common.net.InetAddresses
import io.netty.buffer.ByteBufAllocator
import katium.client.qq.network.codec.crypto.QQTeaCipher
import katium.client.qq.network.codec.jce.SimpleJceStruct
import katium.client.qq.network.codec.jce.readJceStruct
import katium.client.qq.network.codec.struct.*
import katium.core.util.netty.buffer
import katium.core.util.okhttp.GlobalHttpClient
import katium.core.util.okhttp.await
import katium.core.util.okhttp.expected
import katium.core.util.okhttp.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.InetSocketAddress

object SsoServerListManager {

    const val SERVER_URL = "https://configsvr.msf.3g.qq.com/configsvr/serverlist.jsp?mType=getssolist"

    /**
     * Key: F0441F5FF42DA58FDCF7949ABA62D411(aka as 0xF0441F5Fu, 0xF42DA58Fu, 0xDCF7949Au, 0xBA62D411u)
     */
    @JvmField
    val CIPHER = QQTeaCipher(0xF0441F5Fu, 0xF42DA58Fu, 0xDCF7949Au, 0xBA62D411u)

    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger("SsoServerListManager")

    suspend fun fetch(): List<SsoServerRecord> {
        LOGGER.trace("Fetching SSO server list...")
        val payload = CIPHER.encrypt(ByteBufAllocator.DEFAULT.buffer {
            writeIntLvPacket {
                writeBytes(
                    RequestPacket(
                        version = 3,
                        servantName = "ConfigHttp",
                        functionName = "HttpServerListReq",
                        buffer = RequestDataV3(
                            "HttpServerListReq" to SsoListRequestData().dump().wrapUniRequestData()
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
                val records = (this.map["HttpServerListRes"]!!.decodeUniRequestData()
                    .readJceStruct()[2u] as Collection<SimpleJceStruct>)
                    .map(::SsoServerRecord)
                LOGGER.trace("Got ${records.size} SSO server records")
                return records
            }
        }
    }

    suspend fun getServerAddresses(): Set<InetSocketAddress> {
        val records = fetch()
        LOGGER.trace("Resolving SSO server addresses...")
        val addresses = mutableSetOf<InetSocketAddress>()
        // IP addresses
        addresses.addAll(records.filter { InetAddresses.isInetAddress(it.address) }
            .map { InetSocketAddress(it.address, it.port) })
        // Domains
        addresses.addAll(records.filter { !InetAddresses.isInetAddress(it.address) }
            .flatMap { record ->
                withContext(Dispatchers.IO) { InetAddress.getAllByName(record.address) }
                    .map { InetSocketAddress(it, record.port) }
            })
        // Default server
        addresses.addAll(withContext(Dispatchers.IO) { InetAddress.getAllByName("msfwifi.3g.qq.com") }
            .map { InetSocketAddress(it, 8080) })
        LOGGER.trace("Resolved ${addresses.size} SSO server addresses")
        return addresses
    }

}