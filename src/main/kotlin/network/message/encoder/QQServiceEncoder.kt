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

import com.google.protobuf.ByteString
import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQService
import katium.client.qq.network.QQClient
import katium.client.qq.network.pb.PbMessageElements
import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterOutputStream

object QQServiceEncoder : MessageEncoder<QQService> {

    override val shouldStandalone: Boolean get() = true

    override suspend fun encode(
        client: QQClient, context: QQChat, message: QQService, withGeneralFlags: Boolean, isStandalone: Boolean
    ): Array<PbMessageElements.Element> = ByteArrayOutputStream().use { content ->
        content.write(1)
        DeflaterOutputStream(content).use {
            it.write(message.content.toByteArray())
        }
        val richMessage = PbMessageElements.Element.newBuilder().setServiceMessage(
            PbMessageElements.ServiceMessage.newBuilder().setTemplate(ByteString.copyFrom(content.toByteArray()))
                .setServiceID(message.id)
        ).build()
        return run {
            if (message.id == 1) {
                arrayOf(
                    PbMessageElements.Element.newBuilder().setText(
                        PbMessageElements.Text.newBuilder().setString(message.resourceID).build()
                    ).build(), richMessage
                )
            } else {
                arrayOf(richMessage)
            }
        } + (if (withGeneralFlags) arrayOf(
            if (message.type == QQService.Type.LONG_MESSAGE) {
                PbMessageElements.Element.newBuilder().setGeneralFlags(
                    PbMessageElements.GeneralFlags.newBuilder().setLongTextFlag(1)
                        .setLongTextResourceID(message.resourceID).setPbReserve(ByteString.fromHex("7800F80100C80200"))
                        .build()
                ).build()
            } else {
                PbMessageElements.Element.newBuilder().setGeneralFlags(
                    PbMessageElements.GeneralFlags.newBuilder()
                        .setPbReserve(ByteString.fromHex("08097800C80100F00100F80100900200C80200980300A00320B00300C00300D00300E803008A04020803900480808010B80400C00400"))
                        .build()
                ).build()
            }
        ) else emptyArray())
    }

}