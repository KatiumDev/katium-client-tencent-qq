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

import com.google.common.hash.Hashing
import io.netty.channel.socket.SocketChannel
import katium.client.qq.network.codec.highway.pipeline.HighwayFrameDecoder
import katium.client.qq.network.codec.highway.pipeline.HighwayFrameEncoder
import katium.client.qq.network.codec.highway.pipeline.HighwayResponseHandler
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

class HighwaySession(val highway: Highway, val channel: SocketChannel) : AutoCloseable {

    init {
        channel.pipeline().addLast("HighwayFrameEncoder", HighwayFrameEncoder())
            .addLast("HighwayFrameDecoder", HighwayFrameDecoder())
            .addLast("HighwayResponseHandler", HighwayResponseHandler(this))
    }

    var responseContinuation: AtomicRef<Continuation<HighwayResponseFrame>?> = atomic(null)

    suspend fun sendAndWait(frame: HighwayRequestFrame) = suspendCoroutine {
        if (responseContinuation.getAndSet(it) != null) {
            throw IllegalStateException("Another coroutine already waiting for response")
        }
        channel.writeAndFlush(frame).sync()
    }

    override fun close() {
        channel.close().sync()
    }

    fun createDataHeader(
        command: String, flag: Int = 4096, commandID: Int, locale: Int = 2052
    ) = PbHighway.DataHeader(
        version = 1,
        uin = highway.client.uin.toString(),
        command = command,
        sequence = highway.allocSequenceID(),
        appID = highway.client.version.appID,
        flag = flag,
        commandID = commandID,
        localeID = locale,
        buildVersion = highway.client.version.version, // @TODO: test
        retryTimes = 0
    )

    suspend fun sendEcho() {
        sendAndWait(PbHighway.RequestHeader(data = createDataHeader(command = "PicUp.Echo", commandID = 0)) to null)
    }

    suspend fun sendChunk(transaction: HighwayTransaction, chunk: Int): HighwayResponseFrame {
        val bodySize = transaction.body.size
        val chunkOffset = transaction.chunkSize * chunk
        val chunkSize = min(bodySize - chunkOffset, transaction.chunkSize)
        val chunkData = transaction.body.copyOfRange(chunkOffset, chunkOffset + chunkSize)
        @Suppress("DEPRECATION") return sendAndWait(
            PbHighway.RequestHeader(
                data = createDataHeader(command = "PicUp.DataUp", commandID = transaction.command),
                segment = PbHighway.SegmentHeader(
                    fileSize = bodySize.toLong(),
                    dataOffset = chunkOffset.toLong(),
                    dataLength = chunkSize,
                    serviceTicket = transaction.ticket,
                    md5 = Hashing.md5().hashBytes(chunkData).asBytes(),
                    fileMd5 = transaction.bodyMd5
                )
            ) to chunkData
        )
    }

}