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
package katium.client.qq.network.sso

import com.google.common.net.InetAddresses
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import katium.client.qq.network.codec.base.writeWithIntLength
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.client.qq.network.codec.jce.SimpleJceStruct
import katium.client.qq.network.codec.jce.readJceStruct
import katium.client.qq.network.codec.taf.RequestDataV3
import katium.client.qq.network.codec.taf.RequestPacket
import katium.client.qq.network.codec.taf.unwrapUniRequestData
import katium.client.qq.network.codec.taf.wrapUniRequestData
import katium.core.util.netty.buffer
import katium.core.util.netty.use
import katium.core.util.okhttp.GlobalHttpClient
import katium.core.util.okhttp.await
import katium.core.util.okhttp.expected
import katium.core.util.okhttp.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object SsoServerListManager {

    const val SERVER_URL = "https://configsvr.msf.3g.qq.com/configsvr/serverlist.jsp?mType=getssolist"

    /**
     * Key: F0441F5FF42DA58FDCF7949ABA62D411(aka as 0xF0441F5Fu, 0xF42DA58Fu, 0xDCF7949Au, 0xBA62D411u)
     */
    @JvmField
    val CIPHER = QQTeaCipher(0xF0441F5Fu, 0xF42DA58Fu, 0xDCF7949Au, 0xBA62D411u)

    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger("SsoServerListManager")

    val qualityTestEventLoopGroup = NioEventLoopGroup()

    suspend fun fetchRecords(): List<SsoServerRecord> {
        LOGGER.info("Fetching SSO server list...")
        val response = GlobalHttpClient.newCall(
            Request.Builder()
                .url(SERVER_URL)
                .post(buildRequestPayload().toRequestBody(true))
                .build()
        ).await().expected(200)
        lateinit var records: List<SsoServerRecord>
        CIPHER.decrypt(PooledByteBufAllocator.DEFAULT.buffer(response.body.bytes()))
            .skipBytes(4)
            .use {
                RequestPacket(
                    it.readJceStruct()
                ).use { packet ->
                    RequestDataV3(packet.buffer.readJceStruct()).use { data ->
                        @Suppress("UNCHECKED_CAST")
                        records = (data["HttpServerListRes"]!!.unwrapUniRequestData()
                            .readJceStruct()[2u] as Collection<SimpleJceStruct>)
                            .map(::SsoServerRecord)
                        LOGGER.info("Got ${records.size} SSO server records")
                    }
                }
            }
        return records
    }

    fun buildRequestPacket() = RequestPacket(
        version = 3,
        servantName = "ConfigHttp",
        functionName = "HttpServerListReq",
        buffer = RequestDataV3(
            "HttpServerListReq" to SsoListRequestData().dump().wrapUniRequestData()
        ).dump()
    )

    fun buildRequestPayload() = CIPHER.encrypt(PooledByteBufAllocator.DEFAULT.buffer {
        buildRequestPacket().dump().use {
            writeWithIntLength(it)
        }
    })

    suspend fun resolveRecords(records: Collection<SsoServerRecord>): Set<InetSocketAddress> {
        LOGGER.info("Resolving SSO server addresses...")
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
        LOGGER.info("Resolved ${addresses.size} server addresses")
        return addresses
    }

    suspend fun fetchAddresses() = resolveRecords(fetchRecords())

    suspend fun fetchSortedAddresses(): Set<InetSocketAddress> {
        val addresses = fetchAddresses()
        val result = mutableMapOf<InetSocketAddress, Int>()
        LOGGER.info("Starting server quality test...")
        coroutineScope {
            withContext(Dispatchers.IO) {
                addresses.forEach {
                    launch {
                        result[it] = doQualityTest(it)
                    }
                }
            }
        }
        return addresses.sortedBy { result[it] }.toSet()
    }

    suspend fun doQualityTest(address: InetSocketAddress): Int {
        val startTime = System.currentTimeMillis()
        return suspendCoroutine { continuation ->
            Bootstrap()
                .channel(NioSocketChannel::class.java)
                .group(qualityTestEventLoopGroup)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                    }
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .connect(address)
                .addListener {
                    if (it.isSuccess) {
                        continuation.resume((System.currentTimeMillis() - startTime).toInt())
                    } else {
                        continuation.resume(9999)
                    }
                }
        }
    }

    suspend fun fetchAddressesForConnection() = fetchSortedAddresses().run {
        reversed().drop(size / 2).reversed().toSet()
    }

}