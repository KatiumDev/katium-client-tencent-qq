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
import com.google.protobuf.ByteString
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import katium.client.qq.QQBot
import katium.client.qq.group.QQGroup
import katium.client.qq.network.auth.DeviceInfo
import katium.client.qq.network.auth.LoginSigInfo
import katium.client.qq.network.auth.ProtocolType
import katium.client.qq.network.codec.crypto.tea.QQTeaCipher
import katium.client.qq.network.codec.crypto.tea.TeaCipher
import katium.client.qq.network.codec.highway.Highway
import katium.client.qq.network.codec.oicq.OicqPacketCodec
import katium.client.qq.network.codec.packet.TransportPacket
import katium.client.qq.network.codec.pipeline.*
import katium.client.qq.network.event.QQChannelInitializeEvent
import katium.client.qq.network.handler.*
import katium.client.qq.network.message.decoder.MessageDecoders
import katium.client.qq.network.message.encoder.MessageEncoders
import katium.client.qq.network.message.parser.MessageParsers
import katium.client.qq.network.packet.chat.*
import katium.client.qq.network.packet.login.LoginResponsePacket
import katium.client.qq.network.packet.login.PasswordLoginPacket
import katium.client.qq.network.packet.login.UpdateSigRequest
import katium.client.qq.network.packet.meta.ClientRegisterPacket
import katium.client.qq.network.packet.review.PullGroupSystemMessagesRequest
import katium.client.qq.network.packet.review.PullGroupSystemMessagesResponse
import katium.client.qq.network.pb.PbSessionToken
import katium.client.qq.network.sso.SsoServerListManager
import katium.client.qq.network.sync.Synchronizer
import katium.client.qq.user.QQContact
import katium.client.qq.user.QQUser
import katium.client.qq.util.CoroutineLazy
import katium.core.event.BotOfflineEvent
import katium.core.event.BotOnlineEvent
import katium.core.review.ReviewMessage
import katium.core.util.event.post
import katium.core.util.event.register
import katium.core.util.netty.EmptyByteBuf
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okio.IOException
import java.io.Closeable
import java.io.File
import java.net.InetSocketAddress
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.random.Random

class QQClient(val bot: QQBot) : CoroutineScope by bot, Closeable {

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

    val serverAddresses = selectServerAddresses()
    var currentServerCounter = 0
    val currentServerAddress get() = serverAddresses[currentServerCounter]
    var retryTimes = 0

    private fun selectServerAddresses() = if (bot.options.remoteServerAddress != null) {
        mutableListOf(InetSocketAddress(bot.options.remoteServerAddress, bot.options.remoteServerPort!!))
    } else {
        runBlocking(coroutineContext) {
            SsoServerListManager.fetchAddressesForConnection().toMutableList()
        }
    }

    val eventLoopGroup = NioEventLoopGroup()
    var channel: SocketChannel? = null
    val packetHandlers: MutableMap<Int, Continuation<TransportPacket.Response>> = mutableMapOf()

    val isConnected get() = channel?.isActive ?: false
    val isOnline get() = isConnected && isLoggedIn
    var isLoggedIn: Boolean = false

    val deviceInfo = selectDeviceInfo()
    val version = selectClientVersion()
    val passwordMD5: ByteArray by lazy {
        @Suppress("DEPRECATION")
        if (bot.options.passwordMD5 != null) HexFormat.of().parseHex(bot.options.passwordMD5)
        else Hashing.md5().hashBytes(bot.options.password!!.toByteArray()).asBytes()
    }

    private fun selectDeviceInfo() = if (bot.options.deviceInfoFile != null) {
        logger.info("Reading device info from ${bot.options.deviceInfoFile}")
        Json.decodeFromString(File(bot.options.deviceInfoFile).readText())
    } else {
        logger.info("Using default device info")
        DeviceInfo()
    }

    private fun selectClientVersion() = if (bot.options.clientVersionFile != null) {
        logger.info("Reading client version info from ${bot.options.clientVersionFile}")
        Json.decodeFromString(File(bot.options.clientVersionFile).readText())
    } else {
        logger.info("Using builtin client version info for ${bot.options.protocolType}")
        ProtocolType.valueOf(bot.options.protocolType).builtinVersion
    }

    val sig = LoginSigInfo(ksid = deviceInfo.computeKsid())
    val sessionFile = bot.dataPath.resolve("qq.session_token.dat")
    val oicqCodec = OicqPacketCodec(this)
    val highway = Highway(this)
    var heartbeatJob: Job? = null
    val synchronzier = Synchronizer(this)
    val messageParsers = MessageParsers(this)
    val messageDecoders = MessageDecoders(this)
    val messageEncoders = MessageEncoders(this)

    internal fun loadSession(): Boolean {
        if (sessionFile.exists()) {
            val session = PbSessionToken.SessionToken.parseFrom(sessionFile.readBytes())
            if (session.uin != uin) {
                logger.warn("Session cache found at ${sessionFile.absolutePathString()}, but uin is not matched")
                return false
            }
            sig.d2 = session.d2.toByteArray().toUByteArray()
            sig.d2KeyEncoded = session.d2Key.toByteArray().toUByteArray()
            synchronized(sig) { sig.d2Key = TeaCipher.decodeByteKey(sig.d2KeyEncoded) }
            sig.tgt = session.tgt.toByteArray().toUByteArray()
            deviceInfo.tgtgtKey = session.tgtgtKey.toByteArray()
            if (session.hasT133())
                sig.t133 = session.t133.toByteArray().toUByteArray()
            if (session.hasEncryptedA1())
                sig.encryptedA1 = session.encryptedA1.toByteArray().toUByteArray()
            if (session.hasWtSessionTicketKey()) {
                oicqCodec.wtSessionTicketKey = session.wtSessionTicketKey.toByteArray()
                oicqCodec.wtSessionTicketKeyCipher = QQTeaCipher(oicqCodec.wtSessionTicketKey!!.toUByteArray())
            }
            logger.info("Session cache loaded from ${sessionFile.absolutePathString()}")
            return true
        } else return false
    }

    internal fun writeSession() {
        sessionFile.writeBytes(
            PbSessionToken.SessionToken.newBuilder()
                .setUin(uin)
                .setD2(ByteString.copyFrom(sig.d2.toByteArray()))
                .setD2Key(ByteString.copyFrom(sig.d2KeyEncoded.toByteArray()))
                .setTgt(ByteString.copyFrom(sig.tgt.toByteArray()))
                .setTgtgtKey(ByteString.copyFrom(deviceInfo.tgtgtKey))
                .apply { if (sig.t133 != null) t133 = ByteString.copyFrom(sig.t133!!.toByteArray()) }
                .apply {
                    if (sig.encryptedA1 != null) encryptedA1 = ByteString.copyFrom(sig.encryptedA1!!.toByteArray())
                }
                .apply {
                    if (oicqCodec.wtSessionTicketKey != null) wtSessionTicketKey =
                        ByteString.copyFrom(oicqCodec.wtSessionTicketKey!!)
                }
                .build()
                .toByteArray()
        )
        logger.info("Session cache wrote")
    }

    lateinit var reviewMessages: Set<ReviewMessage>
    var cachedFriends = CoroutineLazy(this, ::pullFriends)
    var cachedGroups = CoroutineLazy(this, ::pullGroups)

    // @TODO: reconnect on disconnected
    suspend fun connect() {
        while (retryTimes <= bot.options.ssoRetryTimes) {
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
            delay(bot.options.ssoRetryDelay)
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

    override fun close() {
        channel!!.close().sync()
    }

    val packetSequenceID = atomic(Random.Default.nextInt())
    val groupMessageSequenceID = atomic(Random.Default.nextInt(20000))
    val friendMessageSequenceID = atomic(Random.Default.nextInt(20000))
    fun allocPacketSequenceID() = packetSequenceID.incrementAndGet()
    fun allocGroupMessageSequenceID() = groupMessageSequenceID.addAndGet(2)
    fun allocFriendMessageSequenceID() = friendMessageSequenceID.incrementAndGet()

    fun send(packet: TransportPacket.Request) {
        println("sent ${packet.command}, ${packet.sequenceID}")
        channel!!.writeAndFlush(packet).sync()
    }

    suspend fun sendAndWait(packet: TransportPacket.Request): TransportPacket.Response = suspendCoroutine {
        packetHandlers[packet.sequenceID] = it
        send(packet)
    }

    suspend fun sendAndWaitOicq(packet: TransportPacket.Request) = sendAndWait(packet) as TransportPacket.Response.Oicq

    suspend fun login() {
        if (bot.options.cacheSession && loadSession()) {
            loginWithToken()
        } else {
            logger.info("Session cache not found or disabled")
            loginWithPassword()
        }
        registerClient()
        notifyOnline()
        pullSystemMessages()
        startSyncMessages()
        logger.info("Login succeeded")
    }

    suspend fun loginWithToken() =
        sendAndWaitOicq(UpdateSigRequest.create(this, mainSigMap = version.mainSigMap)).close()

    suspend fun loginWithPassword() = sendAndWaitOicq(PasswordLoginPacket.create(this)).use {
        val response = it.packet as LoginResponsePacket
        if (!response.success) {
            throw RuntimeException("Password login failed, $response")
        }
    }

    suspend fun registerClient() {
        runCatching {
            sendAndWait(ClientRegisterPacket.create(this))
            isLoggedIn = true
        }.onFailure {
            isLoggedIn = false
        }.getOrThrow()
    }

    internal suspend fun notifyOnline() {
        isLoggedIn = true
        if(bot.options.cacheSession) writeSession()
        bot.post(BotOnlineEvent(bot))
    }

    internal suspend fun notifyOffline() {
        isLoggedIn = false
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

    suspend fun getFriends() = cachedFriends.get()

    fun getFriendsSync() = cachedFriends.getSync()

    suspend fun pullFriends(): Map<Long, QQContact> {
        logger.info("Pulling friend list")
        var totalCount = Int.MAX_VALUE
        val friends = mutableMapOf<Long, QQContact>()
        while (friends.size < totalCount) {
            val response = (sendAndWait(
                PullFriendListRequest.create(
                    this,
                    friends = friends.size.toShort() to 150,
                    groups = 0.toShort() to 0
                )
            ) as PullFriendListResponse).response
            totalCount = response.totalFriendsCount.toInt()
            friends += response.friends
                .asSequence()
                .map { PullFriendListResponseData.Friend(it) }
                .map { QQUser(bot = bot, id = it.uin, name = it.nickName, isContact = true) }
                .map(QQUser::asContact)
                .requireNoNulls()
                .associateBy { it.id }
        }
        logger.info("Got ${friends.size} friends")
        return friends.toMap()
    }

    suspend fun getGroups() = cachedGroups.get()

    fun getGroupsSync() = cachedGroups.getSync()

    suspend fun pullGroups(): Map<Long, QQGroup> {
        logger.info("Pulling group list")
        val groups = mutableMapOf<Long, QQGroup>()
        var cookies: ByteBuf = EmptyByteBuf
        do {
            val response = (sendAndWait(
                PullGroupListRequest.create(
                    this,
                    cookies = cookies
                )
            ) as PullGroupListResponse).response
            cookies = response.cookies
            cookies.retain()
            groups += response.troopList
                .asSequence()
                .map { PullGroupListResponseData.Troop(it) }
                .map { QQGroup(bot = bot, id = it.groupCode, name = it.groupName, isContact = true) }
                .requireNoNulls()
                .associateBy { it.id }
        } while (cookies.isReadable)
        cookies.release()
        logger.info("Got ${groups.size} groups")
        return groups.toMap()
    }

}