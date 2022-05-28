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
import katium.client.qq.network.handler.*
import katium.client.qq.network.message.decoder.MessageDecoders
import katium.client.qq.network.message.encoder.MessageEncoder
import katium.client.qq.network.message.encoder.MessageEncoders
import katium.client.qq.network.message.parser.MessageParsers
import katium.client.qq.network.packet.messageSvc.PullMessagesRequest
import katium.client.qq.network.packet.profileSvc.PullGroupSystemMessagesRequest
import katium.client.qq.network.packet.profileSvc.PullGroupSystemMessagesResponse
import katium.client.qq.network.packet.statSvc.ClientRegisterPacket
import katium.client.qq.network.packet.wtlogin.LoginResponsePacket
import katium.client.qq.network.packet.wtlogin.PasswordLoginPacket
import katium.client.qq.network.sso.SsoServerListManager
import katium.client.qq.network.sync.Synchronizer
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
        bot.register(ConfigPushHandler)
        bot.register(SidTicketExpiredHandler)
        bot.register(FriendMessagesHandler)
        bot.register(GroupMessagesHandler)
        bot.register(RawMessageHandler)
        bot.register(ReadReportHandler)
    }

    val serverAddresses: MutableList<InetSocketAddress> by lazy {
        if ("qq.remote_server_address" in bot.config) {
            mutableListOf(
                InetSocketAddress(
                    bot.config["qq.remote_server_address"]!!, (bot.config["qq.remote_server_port"]
                        ?: throw IllegalArgumentException("qq.remote_server_port not found but qq.remote_server_address set")).toInt()
                )
            )
        } else {
            runBlocking(coroutineContext) {
                SsoServerListManager.fetchAddressesForConnection().toMutableList()
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
    val oicqCodec = OicqPacketCodec(this)
    var heartbeatJob: Job? = null
    var lastMessageTime = 0L
    val synchronzier = Synchronizer(this)
    val messageDecoders = MessageDecoders(this)
    val messageParsers = MessageParsers(this)
    val messageEncoders = MessageEncoders(this)

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

    val packetSequenceID = atomic(Random.Default.nextInt())
    val groupMessageSequenceID = atomic(Random.Default.nextInt(20000))
    fun allocPacketSequenceID() = packetSequenceID.incrementAndGet()
    fun allocGroupMessageSequenceID() = groupMessageSequenceID.addAndGet(2)

    fun send(packet: TransportPacket.Request) {
        println("sent ${packet.command}, ${packet.sequenceID}")
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
                notifyOnline()
                pullSystemMessages()
                startSyncMessages()
                logger.info("Login succeeded")
            } else {
                throw RuntimeException("Login failed, $response")
            }
        }
    }

    suspend fun registerClient() {
        runCatching {
            sendAndWait(ClientRegisterPacket.create(this))
            isClientRegistered = true
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
        val safeMessages = (sendAndWait(PullGroupSystemMessagesRequest.create(this, suspicious = false))
                as PullGroupSystemMessagesResponse).messages
        val suspiciousMessages = (sendAndWait(PullGroupSystemMessagesRequest.create(this, suspicious = true))
                as PullGroupSystemMessagesResponse).messages
        reviewMessages = (safeMessages + suspiciousMessages).toSet()
    }

    fun startSyncMessages() =
        send(PullMessagesRequest.create(this))

}