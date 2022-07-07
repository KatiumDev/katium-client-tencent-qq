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

import katium.client.qq.chat.QQChat
import katium.client.qq.message.content.QQService
import katium.client.qq.network.QQClient
import katium.client.qq.network.message.pb.PbMessageElement
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.DeflaterOutputStream

object QQServiceEncoder : MessageEncoder<QQService> {

    override val shouldStandalone: Boolean get() = true

    override suspend fun encode(
        client: QQClient, context: QQChat, message: QQService, withGeneralFlags: Boolean, isStandalone: Boolean
    ): Array<PbMessageElement> = ByteArrayOutputStream().use { content ->
        content.write(1)
        DeflaterOutputStream(content).use {
            it.write(message.content.toByteArray())
        }
        val richMessage = PbMessageElement(
            serviceMessage = PbMessageElement.ServiceMessage(
                template = content.toByteArray(),
                serviceID = message.id
            )
        )
        return run {
            if (message.id == 1) {
                arrayOf(PbMessageElement(text = PbMessageElement.Text(string = message.resourceID)), richMessage)
            } else {
                arrayOf(richMessage)
            }
        } + (if (withGeneralFlags) arrayOf(
            if (message.type == QQService.Type.LONG_MESSAGE) {
                PbMessageElement(
                    generalFlags = PbMessageElement.GeneralFlags(
                        longTextFlag = 1,
                        longTextResourceID = message.resourceID,
                        pbReserve = HexFormat.of().parseHex("7800F80100C80200")
                    )
                )
            } else {
                PbMessageElement(
                    generalFlags = PbMessageElement.GeneralFlags(
                        pbReserve = HexFormat.of()
                            .parseHex("08097800C80100F00100F80100900200C80200980300A00320B00300C00300D00300E803008A04020803900480808010B80400C00400")
                    )
                )
            }
        ) else emptyArray())
    }

}