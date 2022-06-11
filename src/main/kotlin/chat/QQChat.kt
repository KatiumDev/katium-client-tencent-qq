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
package katium.client.qq.chat

import com.google.common.hash.HashCode
import com.google.common.hash.Hashing
import com.google.protobuf.ByteString
import katium.client.qq.QQBot
import katium.client.qq.QQLocalChatID
import katium.client.qq.asQQ
import katium.client.qq.group.QQGroup
import katium.client.qq.message.QQMessage
import katium.client.qq.message.content.QQService
import katium.client.qq.network.codec.highway.HighwayTransaction
import katium.client.qq.network.packet.chat.MultiMessagesUploadRequest
import katium.client.qq.network.packet.chat.MultiMessagesUploadResponse
import katium.client.qq.network.packet.chat.SendMessageRequest
import katium.client.qq.network.packet.chat.SendMessageResponse
import katium.client.qq.network.pb.PbLongMessages
import katium.client.qq.network.pb.PbMessagePackets
import katium.client.qq.network.pb.PbMessages
import katium.client.qq.network.pb.PbMultiMessages
import katium.client.qq.user.QQContact
import katium.core.chat.Chat
import katium.core.chat.ChatInfo
import katium.core.event.MessagePreSendEvent
import katium.core.group.Group
import katium.core.message.Message
import katium.core.message.MessageRef
import katium.core.message.content.Forward
import katium.core.message.content.MessageChain
import katium.core.message.content.MessageContent
import katium.core.util.event.post
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.GZIPOutputStream
import kotlin.random.Random

class QQChat(override val bot: QQBot, id: Long, context: ChatInfo, val routingHeader: PbMessagePackets.RoutingHeader) :
    Chat(bot, QQLocalChatID(id), context) {

    override suspend fun sendMessage(content: MessageContent) = sendMessage(content, false)

    private suspend fun sendMessage(content: MessageContent, isStandalone: Boolean): MessageRef? {
        val client = bot.client
        return bot.post(MessagePreSendEvent(bot, this, content.simplest))?.let {
            var (standaloneParts, mainParts) = it.content.select { part -> client.messageEncoders.shouldStandalone(part) }
            runCatching {
                if (mainParts.isEmpty()) {
                    mainParts = standaloneParts
                    standaloneParts = emptyArray()
                }
                if (mainParts.isEmpty()) return null
                if (!isStandalone) { // check max count in one message
                    mainParts.sortByDescending { part -> client.messageEncoders.getPriority(part) }
                    mainParts.mapNotNull { part -> client.messageEncoders.find(part::class) }.toSet()
                        .filter { (_, encoder) -> encoder.maxCountOneMessage != null }.forEach { (type, encoder) ->
                            var count = 0
                            mainParts.filter { part -> type.isInstance(part) }.toSet().forEach { part ->
                                count++
                                if (count > encoder.maxCountOneMessage!!) {
                                    run { // remove from mainParts
                                        val oldParts = mainParts
                                        mainParts = Array(mainParts.size - 1) { MessageChain.EMPTY }
                                        var index = 0
                                        oldParts.forEach { oldPart ->
                                            if (oldPart != part) {
                                                mainParts[index] = oldPart
                                                index++
                                            }
                                        }
                                    }
                                    standaloneParts += part
                                }
                            }
                        }
                }
                val isGroup = context is QQGroup
                val messageSequence = if (isGroup) client.allocGroupMessageSequenceID()
                else client.allocFriendMessageSequenceID()
                val messageRandom = Random.Default.nextInt()
                val time = System.currentTimeMillis() / 1000
                val response = client.sendAndWait(
                    SendMessageRequest.create(client,
                        messageSequence = messageSequence,
                        routingHeader = routingHeader,
                        elements = client.messageEncoders.encode(
                            this,
                            MessageChain(*mainParts).simplest,
                            withGeneralFlags = isGroup,
                            isStandalone = isStandalone,
                        ),
                        messageRandom = messageRandom,
                        syncCookieTime = if (isGroup) null else time,
                        forward = mainParts.any { part -> part is QQService || part is Forward })
                ) as SendMessageResponse
                if (response.errorMessage != null) {
                    throw IllegalStateException(response.errorMessage)
                }
                if (isGroup) {
                    val group = context as QQGroup
                    for (i in 0..14) {
                        val message = group.pullHistoryMessages(
                            group.lastReadSequence.get() - 10, group.lastReadSequence.get() + 1
                        ).find { message -> message.messageRandom == messageRandom }
                        if (message != null) {
                            return@runCatching message.ref
                        }
                        delay(100L * i)
                    }
                    client.logger.error("Unable to pull back message, sequence=$messageSequence, random=$messageRandom, content=$content")
                }
                val message = QQMessage(
                    bot,
                    this@QQChat,
                    bot.selfInfo,
                    it.content,
                    time = time * 1000,
                    sequence = messageSequence,
                    messageRandom = messageRandom
                )
                message.ref
            }.onSuccess {
                standaloneParts.forEach { part -> sendMessage(part, true) }
            }.getOrThrow()
        }
    }

    override suspend fun removeMessage(ref: MessageRef) = if (this.contextContact != null) {
        val message = ref.message!! as QQMessage
        (context as QQContact).recallMessage(message.sequence, message.messageRandom, message.time / 1000)
    } else {
        val message = ref.message!! as QQMessage
        (context as QQGroup).recallMessage(message.sequence, message.messageRandom)
    }

    suspend fun uploadImage(data: ByteArray) = if (this.contextContact != null) {
        (context as QQContact).uploadImage(data)
    } else {
        (context as QQGroup).uploadImage(data)
    }

    suspend fun findImage(md5: HashCode, size: Int) = if (this.contextContact != null) {
        val contact = (context as QQContact)
        val result = contact.queryImage(md5, size)
        if (result.isExists) result else contact.queryGroupImage(md5, size)
    } else {
        (context as QQGroup).queryImage(md5, size)
    }

    suspend fun resolveImageUrl(md5: HashCode, size: Int) =
        findImage(md5, size).contentUrl ?: "https://gchat.qpic.cn/gchatpic_new/0/0-0-${
            HexFormat.of().formatHex(md5.asBytes()).uppercase()
        }/0"

    suspend fun uploadMultiMessages(messages: List<Message>): String {
        val groupCode = context.localID.asQQ.uin
        val groupUin = if (context is Group) bot.getGroup(groupCode)!!.groupUin.get() else groupCode
        val pbMessages = messages.map {
            val qqMessage = it as? QQMessage
            PbMessages.Message.newBuilder().setHeader(
                PbMessages.MessageHeader.newBuilder().setFromUin(it.senderUser!!.localID.asQQ.uin)
                    .setSequence(qqMessage?.sequence ?: 0).setTime((it.time / 1000).toInt())
                    .setUid(0x0100000000000000 or ((qqMessage?.messageRandom ?: 0).toLong() and 0xFFFFFFFF))
                    .setMultiTransHeader(
                        PbMessages.MultiTransHeader.newBuilder().setMessageID(1)
                    ).setType(82).setGroupInfo(
                        PbMessages.GroupInfo.newBuilder().setGroupCode(groupCode).setGroupRank(ByteString.empty())
                            .setGroupName(ByteString.empty()).setGroupCard(it.senderUser!!.name)
                    )
            ).setBody(
                PbMessages.MessageBody.newBuilder().setRichText(
                    PbMessages.RichText.newBuilder().addAllElements(
                        bot.client.messageEncoders.encode(this, it.content.simplest, isStandalone = false)
                    )
                )
            ).build()
        }
        val body = ByteArrayOutputStream().use {
            GZIPOutputStream(it).use { gzip ->
                gzip.write(
                    PbMultiMessages.MultiMessagesHighwayBody.newBuilder().addAllMessage(pbMessages).addItem(
                        PbMultiMessages.MultiMessagesHighwayItem.newBuilder().setFileName("MultiMsg").setBuffer(
                            PbMultiMessages.MultiMessagesHighwayNew.newBuilder().addAllMessage(pbMessages)
                        )
                    ).build().toByteArray()
                )
            }
            it.toByteArray()
        }

        @Suppress("DEPRECATION") val bodyHash = Hashing.md5().hashBytes(body)
        val response = (bot.client.sendAndWait(
            MultiMessagesUploadRequest.create(
                bot.client,
                buType = 2,
                groupUin = groupUin,
                size = body.size,
                md5 = bodyHash,
            )
        ) as MultiMessagesUploadResponse)
        if (bot.client.highway.ssoAddresses.isEmpty()) bot.client.highway.ssoAddresses += response.uploadServers
        bot.client.highway.upload(
            HighwayTransaction(
                command = 27,
                ticket = response.result.sig.toByteArray(),
                body = PbLongMessages.LongMessagesRequest.newBuilder().setSubCommand(1).setTermType(5)
                    .setPlatformType(9).addUpload(
                        PbLongMessages.LongMessagesUploadRequest.newBuilder().setType(3).setToUin(groupUin)
                            .setContent(ByteString.copyFrom(body)).setStoreType(2).setUkey(response.result.ukey)
                    ).build().toByteArray()
            )
        )
        return response.result.resourceID
    }

}