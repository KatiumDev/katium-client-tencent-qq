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
package katium.client.qq.network.codec.highway

import com.google.common.primitives.Ints
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import katium.client.qq.network.QQClient
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import okio.IOException
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.coroutines.resume
import kotlin.math.min

class Highway(val client: QQClient) {

    companion object {

        fun decodeIPv4(address: Int): InetAddress =
            Inet4Address.getByAddress("", Ints.toByteArray(address).reversedArray())

        fun decodeIPv6(address: ByteArray): InetAddress =
            Inet6Address.getByAddress("", address)

    }

    var sessionSig: ByteArray? = null
    var sessionKey: ByteArray? = null

    val ssoAddresses = mutableListOf<InetSocketAddress>()
    val otherAddresses = mutableListOf<InetSocketAddress>()

    val sequenceID = atomic(0)

    fun allocSequenceID() = sequenceID.addAndGet(2)

    suspend fun connect(address: InetSocketAddress): SocketChannel {
        lateinit var channel: SocketChannel
        suspendCancellableCoroutine<Unit> { continuation ->
            Bootstrap().channel(NioSocketChannel::class.java).group(client.eventLoopGroup)
                .option(ChannelOption.TCP_NODELAY, client.bot.options.highwayTcpNoDelay)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, client.bot.options.highwayConnectTimeout)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        channel = ch
                    }
                }).connect(address).addListener {
                    if (it.isSuccess) {
                        continuation.resume(Unit)
                    } else {
                        continuation.cancel(it.cause())
                    }
                }
        }
        return channel
    }

    suspend fun tryConnect(): HighwaySession {
        for (i in 0 until client.bot.options.highwayReconnectTimes) {
            val address = ssoAddresses.random()
            try {
                return HighwaySession(this, connect(address))
            } catch (e: Throwable) {
                client.logger.info("Connect to highway SSO server $address failed", e)
            }
        }
        throw IOException("Servers are unreachable")
    }

    suspend fun upload(
        transaction: HighwayTransaction,
        threadCount: Int = if (transaction.body.size > client.bot.options.highwayParallelUploadMinSize) client.bot.options.highwayParallelThreads else 1
    ) {
        val chunkCount = (transaction.body.size + transaction.chunkSize) / transaction.chunkSize
        assert(chunkCount != 0)
        val currentChunk = atomic(0)
        coroutineScope {
            withContext(Dispatchers.IO) {
                for (i in 0 until min(chunkCount, threadCount)) {
                    launch(CoroutineName("Highway Upload #$i")) {
                        tryConnect().use {
                            it.sendEcho()
                            while (true) {
                                val chunk = currentChunk.getAndIncrement()
                                if (chunk >= chunkCount) break
                                val (response, _) = it.sendChunk(transaction, chunk)
                                if (response.errorCode != 0) {
                                    throw IllegalStateException("Highway upload failed: ${response.errorCode}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}