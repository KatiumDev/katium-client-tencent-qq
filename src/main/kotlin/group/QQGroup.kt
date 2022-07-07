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
package katium.client.qq.group

import com.google.common.hash.HashCode
import com.google.common.hash.Hashing
import katium.client.qq.QQBot
import katium.client.qq.QQLocalChatID
import katium.client.qq.chat.QQChat
import katium.client.qq.message.QQMessage
import katium.client.qq.network.codec.highway.HighwayTransaction
import katium.client.qq.network.message.parser.GroupMessageParser
import katium.client.qq.network.packet.chat.*
import katium.client.qq.network.packet.chat.image.ImageUploadResult
import katium.client.qq.network.packet.chat.image.UploadGroupPictureRequest
import katium.client.qq.network.packet.chat.image.UploadGroupPictureResponse
import katium.client.qq.network.pb.RoutingHeader
import katium.core.chat.Chat
import katium.core.group.Group
import katium.core.user.User
import katium.core.util.CoroutineLazy

class QQGroup(override val bot: QQBot, val id: Long, override val name: String, val isContact: Boolean) :
    Group(bot, QQLocalChatID(id)) {

    override val chat: Chat? = if (isContact) QQChat(
        bot, id, this, RoutingHeader(group = RoutingHeader.ToGroup(groupCode = id))
    ) else null

    override val members: Set<User>
        get() = emptySet() // @TODO: get group members

    private val groupInfo = CoroutineLazy(bot) {
        val response = (bot.client.sendAndWait(
            PullGroupInfoRequest.create(
                bot.client, groupCode = id
            )
        ) as PullGroupInfoResponse).response
        if (response.info.isEmpty())
            throw IllegalStateException(String(response.errorInfo!!))
        response.info.first().info
    }

    var lastReadSequence =
        CoroutineLazy(bot) { if (isContact) groupInfo.get().groupCurrentMessageSequence!!.toLong() else 0L }

    var groupUin = CoroutineLazy(bot) { groupInfo.get().groupUin }

    suspend fun uploadImage(data: ByteArray, depth: Int = 0): ImageUploadResult {
        if (depth >= 5) throw IllegalStateException("Unable to upload image")
        @Suppress("DEPRECATION") val md5 = Hashing.md5().hashBytes(data)
        val query = queryImage(md5, data.size)
        if (query.resourceKey == null) throw IllegalStateException(query.toString())
        return if (!query.isExists) {
            if (bot.client.highway.ssoAddresses.isEmpty()) bot.client.highway.ssoAddresses += query.uploadServers
            bot.client.highway.upload(
                HighwayTransaction(
                    command = 2, ticket = query.uploadKey!!, body = data
                )
            )
            uploadImage(data, depth = depth + 1)
        } else {
            query
        }
    }

    suspend fun queryImage(md5: HashCode, size: Int) = (bot.client.sendAndWait(
        UploadGroupPictureRequest.create(
            bot.client, groupCode = id, md5 = md5, fileSize = size
        )
    ) as UploadGroupPictureResponse).result

    suspend fun pullHistoryMessages(beginSequence: Long, endSequence: Long): Collection<QQMessage> {
        val response = bot.client.sendAndWait(
            PullGroupHistoryMessagesRequest.create(
                bot.client, groupCode = id, beginSequence = beginSequence, endSequence = endSequence
            )
        ) as PullGroupHistoryMessagesResponse
        if (response.errorMessage != null) {
            throw IllegalStateException(response.errorMessage)
        } else {
            return response.response.messages.map {
                GroupMessageParser.parse(bot.client, it)
            }
        }
    }

    suspend fun recallMessage(sequence: Int, random: Int) {
        val response = bot.client.sendAndWait(
            RecallMessagesRequest.createGroup(
                bot.client, groupCode = id, sequence = sequence, random = random
            )
        ) as RecallMessagesResponse
        if (response.errorMessage != null) {
            throw IllegalStateException(response.errorMessage)
        }
    }

}