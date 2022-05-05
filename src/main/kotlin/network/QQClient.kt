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

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import katium.client.qq.QQBot
import katium.client.qq.network.codec.auth.DeviceInfo
import katium.client.qq.network.codec.auth.LoginSigInfo
import katium.client.qq.network.codec.auth.ProtocolType
import katium.client.qq.network.sso.SsoServerListManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okio.IOException
import java.io.File
import java.net.InetSocketAddress
import kotlin.coroutines.resume

class QQClient(val bot: QQBot) : CoroutineScope by bot {

    val logger by bot::logger

    val serverAddresses: List<InetSocketAddress> by lazy {
        if ("qq.remote_server_address" in bot.config) {
            listOf(
                InetSocketAddress(
                    bot.config["qq.remote_server_address"]!!,
                    (bot.config["qq.remote_server_port"]
                        ?: throw IllegalArgumentException("qq.remote_server_port not found but qq.remote_server_address set"))
                        .toInt()
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
    lateinit var connection: SocketChannel

    val sigInfo = LoginSigInfo()

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
                        .handler(object : ChannelInitializer<SocketChannel>() {
                            override fun initChannel(ch: SocketChannel) {
                                connection = ch
                                this@QQClient.initChannel()
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
                return
            } catch (e: Throwable) {
                logger.error("Connect to $currentServerAddress failed", e)
            }
        }
        throw IOException("All servers are unreachable")
    }

    private fun initChannel() {
    }

    fun send(data: ByteBuf) {
        connection.writeAndFlush(data)
    }

}