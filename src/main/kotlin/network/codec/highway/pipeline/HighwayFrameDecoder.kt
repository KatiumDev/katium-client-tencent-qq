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
package katium.client.qq.network.codec.highway.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import katium.client.qq.network.pb.PbHighway

class HighwayFrameDecoder : ByteToMessageDecoder() {

    companion object {
        const val MAX_FRAME_SIZE = 1024 * 100 // 100k
    }

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        if (`in`.readableBytes() >= 9) {
            `in`.skipBytes(1)
            val headerSize = `in`.readInt()
            val bodySize = `in`.readInt()
            if (headerSize > MAX_FRAME_SIZE) {
                throw UnsupportedOperationException("Highway response header too big, headerSize: $headerSize")
            }
            if (bodySize > MAX_FRAME_SIZE) {
                throw UnsupportedOperationException("Highway response body too big, bodySize: $bodySize")
            }
            val header = ByteArray(headerSize)
            `in`.readBytes(header)
            val body = if (bodySize == 0) null else {
                val buffer = ByteArray(bodySize)
                `in`.readBytes(buffer)
                buffer.toUByteArray()
            }
            `in`.skipBytes(1)
            out.add(PbHighway.HighwayResponseHeader.parseFrom(header) to body)
        }
    }

}