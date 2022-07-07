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
package katium.client.qq.network.message.encoder

import io.netty.buffer.PooledByteBufAllocator
import katium.client.qq.chat.QQChat
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessageElement
import katium.core.message.content.AtAll
import katium.core.util.netty.toArray

object AtAllEncoder : MessageEncoder<AtAll> {

    const val DISPLAY = "@全体成员"

    override suspend fun encode(
        client: QQClient, context: QQChat, message: AtAll, withGeneralFlags: Boolean, isStandalone: Boolean
    ) = message.run {
        arrayOf(
            PbMessageElement(
                text = PbMessageElement.Text(
                    string = DISPLAY,
                    attribute6Buf = PooledByteBufAllocator.DEFAULT.heapBuffer().writeShort(1) // constant
                        .writeShort(0) // start pos
                        .writeShort(DISPLAY.length).writeByte(1) // flag
                        .writeInt(0) // uin
                        .writeShort(0) // const
                        .toArray(release = true)
                )
            ),
            PbMessageElement(text = PbMessageElement.Text(string = " "))
        )
    }

}