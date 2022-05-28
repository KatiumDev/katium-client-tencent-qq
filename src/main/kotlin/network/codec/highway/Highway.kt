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
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.coroutines.resume
import kotlin.math.min
import kotlin.math.roundToInt

class Highway(val client: QQClient) {

    companion object {

        fun decodeIP(address: Int): InetAddress =
            Inet4Address.getByAddress("", Ints.toByteArray(address).reversedArray())

    }

    lateinit var sigSession: ByteArray
    lateinit var sessionKey: ByteArray

    val ssoAddresses = mutableListOf<InetSocketAddress>()
    val otherAddresses = mutableListOf<InetSocketAddress>()

    val sequenceID = atomic(0)

    fun allocSequenceID() = sequenceID.addAndGet(2)

    suspend fun connect(address: InetSocketAddress): SocketChannel {
        lateinit var channel: SocketChannel
        suspendCancellableCoroutine<Unit> { continuation ->
            Bootstrap()
                .channel(NioSocketChannel::class.java)
                .group(client.eventLoopGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(
                    ChannelOption.CONNECT_TIMEOUT_MILLIS,
                    client.bot.config["qq.highway.connect_timeout"]?.toInt() ?: 3000
                )
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        channel = ch
                    }
                })
                .connect(address)
                .addListener {
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
        for (i in 0 until (client.bot.config["qq.highway.reconnect_times"]?.toInt() ?: 10)) {
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
        threadCount: Int = if (transaction.body.size > (client.bot.config["qq.highway.parallel_upload_min_size"]?.toInt()
                ?: (3 * 1024 * 1024))
        ) (client.bot.config["qq.highway.parallel_upload_threads"]?.toInt() ?: 5) else 1
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
                                if (chunk >= chunkCount)
                                    break
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