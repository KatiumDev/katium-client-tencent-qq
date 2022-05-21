/*
 * Katium Client Tencent QQ: Tencent QQ protocol implementation for Katium
 * Copyright (C) 2022  Katium Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package katium.client.qq.network

import com.google.common.hash.Hashing
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import katium.client.qq.QQBot
import katium.client.qq.network.auth.DeviceInfo
import katium.client.qq.network.auth.LoginSigInfo
import katium.client.qq.network.auth.ProtocolType
import katium.client.qq.network.codec.oicq.OicqPacketCodec
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.pipeline.*
import katium.client.qq.network.event.QQChannelInitializeEvent
import katium.client.qq.network.handler.HeartbeatHandler
import katium.client.qq.network.packet.profileSvc.PullGroupSystemMessagesPacket
import katium.client.qq.network.packet.profileSvc.PushGroupSystemMessagesPacket
import katium.client.qq.network.packet.statSvc.ClientRegisterPacket
import katium.client.qq.network.packet.wtlogin.LoginResponsePacket
import katium.client.qq.network.packet.wtlogin.PasswordLoginPacket
import katium.client.qq.network.sso.SsoServerListManager
import katium.core.event.BotOfflineEvent
import katium.core.event.BotOnlineEvent
import katium.core.review.ReviewMessage
import katium.core.util.event.post
import katium.core.util.event.register
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okio.IOException
import java.io.File
import java.net.InetSocketAddress
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class QQClient(val bot: QQBot) : CoroutineScope by bot {

    val logger by bot::logger
    val uin by bot::uin

    init {
        bot.register(HeartbeatHandler)
    }

    val serverAddresses: List<InetSocketAddress> by lazy {
        if ("qq.remote_server_address" in bot.config) {
            listOf(
                InetSocketAddress(
                    bot.config["qq.remote_server_address"]!!, (bot.config["qq.remote_server_port"]
                        ?: throw IllegalArgumentException("qq.remote_server_port not found but qq.remote_server_address set")).toInt()
                )
            )
        } else {
            runBlocking(coroutineContext) {
                SsoServerListManager.fetchAddressesForConnection().toList()
            }
        }
    }
    var currentServerCounter = 0
    val currentServerAddress get() = serverAddresses[currentServerCounter]
    var retryTimes = 0

    val eventLoopGroup = NioEventLoopGroup()
    var channel: SocketChannel? = null
    val packetHandlers: MutableMap<Int, Continuation<TransportPacket.Response>> = mutableMapOf()

    val isConnected get() = channel?.isActive ?: false
    val isOnline get() = isConnected && isClientRegistered
    var isClientRegistered: Boolean = false

    val deviceInfo by lazy {
        if ("qq.device_info_file" in bot.config) {
            val file = bot.config["qq.device_info_file"]!!
            logger.info("Reading device info from $file")
            File(file).readText().let {
                Json.decodeFromString(it)
            }
        } else {
            logger.info("Using default device info")
            DeviceInfo()
        }
    }
    val clientVersion by lazy {
        if ("qq.client_version_info_file" in bot.config) {
            val file = bot.config["qq.client_version_info_file"]!!
            logger.info("Reading client version info from $file")
            File(file).readText().let {
                Json.decodeFromString(it)
            }
        } else {
            val type = bot.config["qq.protocol_type"] ?: "ANDROID_PHONE"
            logger.info("Using builtin client version info for $type")
            ProtocolType.valueOf(type).builtinVersion
        }
    }
    val protocolType by clientVersion::protocolType
    val passwordMD5: ByteArray by lazy {
        @Suppress("DEPRECATION")
        if ("qq.user.password.md5" in bot.config) HexFormat.of().parseHex(bot.config["qq.user.password.md5"]!!)
        else Hashing.md5().hashBytes(bot.config["qq.user.password"]!!.toByteArray()).asBytes()
    }

    val sig = LoginSigInfo(ksid = deviceInfo.computeKsid())
    val sequenceID = atomic(Random.Default.nextInt())
    val oicqCodec = OicqPacketCodec(this)
    var heartbeatJob: Job? = null

    lateinit var reviewMessages: Set<ReviewMessage>

    // @TODO: reconnect on disconnected
    suspend fun connect() {
        while (retryTimes <= serverAddresses.size) {
            retryTimes++
            currentServerCounter++
            if (currentServerCounter >= serverAddresses.size) {
                currentServerCounter = 0
            }
            logger.info("Trying connect to $currentServerAddress...(retry $retryTimes, server $currentServerCounter/${serverAddresses.size})")
            try {
                suspendCancellableCoroutine<Unit> { continuation ->
                    Bootstrap()
                        .channel(NioSocketChannel::class.java)
                        .group(eventLoopGroup)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(object : ChannelInitializer<SocketChannel>() {
                            override fun initChannel(ch: SocketChannel) {
                                channel = ch
                                this@QQClient.initChannel()
                                runBlocking {
                                    bot.post(QQChannelInitializeEvent(this@QQClient))
                                }
                            }
                        })
                        .connect(currentServerAddress)
                        .addListener {
                            if (it.isSuccess) {
                                continuation.resume(Unit)
                            } else {
                                continuation.cancel(it.cause())
                            }
                        }
                }
                retryTimes = 0
                logger.info("Connected to server")
                login()
                return
            } catch (e: Throwable) {
                logger.error("Connect to $currentServerAddress failed", e)
            }
        }
        throw IOException("All servers are unreachable")
    }

    private fun initChannel() {
        channel!!.pipeline()
            .addLast("RequestPacketEncoder", RequestPacketEncoder(this))
            .addLast("ResponsePacketDecoder", ResponsePacketDecoder(this))
            .addLast("TransportPacketDecoder", TransportPacketDecoder(this))
            .addLast("InboundPacketHandler", InboundPacketHandler(this))
            .addLast("InactiveHandler", InactiveHandler(this))
    }

    fun allocSequenceID() = sequenceID.incrementAndGet()

    fun send(packet: TransportPacket.Request) {
        channel!!.writeAndFlush(packet).sync()
    }

    suspend fun sendAndWait(packet: TransportPacket.Request): TransportPacket.Response = suspendCoroutine {
        packetHandlers[packet.sequenceID] = it
        send(packet)
    }

    suspend fun sendAndWaitOicq(packet: TransportPacket.Request) = sendAndWait(packet) as TransportPacket.Response.Oicq
    suspend fun sendAndWaitBuffered(packet: TransportPacket.Request) =
        sendAndWait(packet) as TransportPacket.Response.Buffered

    suspend fun login() {
        sendAndWaitOicq(PasswordLoginPacket.create(this)).use {
            val response = it.packet as LoginResponsePacket
            if (response.success) {
                registerClient()
                pullSystemMessages()
            } else {
                throw RuntimeException("Login failed, $response")
            }
        }
    }

    suspend fun registerClient() {
        runCatching {
            sendAndWait(ClientRegisterPacket.create(this))
            notifyOnline()
        }.onFailure {
            isClientRegistered = false
        }.getOrThrow()
    }

    internal suspend fun notifyOnline() {
        isClientRegistered = true
        bot.post(BotOnlineEvent(bot))
    }

    internal suspend fun notifyOffline() {
        isClientRegistered = false
        bot.post(BotOfflineEvent(bot))
    }

    suspend fun pullSystemMessages() {
        val safeMessages = (sendAndWait(PullGroupSystemMessagesPacket.create(this, suspicious = false))
                as PushGroupSystemMessagesPacket).messages
        val suspiciousMessages = (sendAndWait(PullGroupSystemMessagesPacket.create(this, suspicious = true))
                as PushGroupSystemMessagesPacket).messages
        reviewMessages = (safeMessages + suspiciousMessages).toSet()
    }

}