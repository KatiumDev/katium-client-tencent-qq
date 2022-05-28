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
package katium.client.qq.user

import com.google.common.hash.HashCode
import com.google.common.hash.Hashing
import katium.client.qq.chat.QQChat
import katium.client.qq.network.codec.highway.HighwayTransaction
import katium.client.qq.network.packet.imgStore.UploadGroupPictureRequest
import katium.client.qq.network.packet.imgStore.UploadGroupPictureResponse
import katium.client.qq.network.packet.longConn.ImageUploadResult
import katium.client.qq.network.packet.longConn.QueryFriendImageRequest
import katium.client.qq.network.packet.longConn.QueryFriendImageResponse
import katium.client.qq.network.pb.PbMessagePackets
import katium.core.chat.Chat
import katium.core.user.Contact

class QQContact(asUser: QQUser) : Contact(asUser) {

    override val bot by asUser::bot
    val id by asUser::id

    override val chat: Chat by lazy {
        QQChat(
            bot, id, this,
            PbMessagePackets.RoutingHeader.newBuilder().setFriend(PbMessagePackets.ToFriend.newBuilder().setToUin(id))
                .build()
        )
    }

    suspend fun uploadImage(data: ByteArray, depth: Int = 0): ImageUploadResult {
        if (depth >= 5) throw IllegalStateException("Unable to upload image")
        @Suppress("DEPRECATION")
        val md5 = Hashing.md5().hashBytes(data)
        val query = queryImage(md5, data.size)
        if (query.resourceKey == null) throw IllegalStateException(query.toString())
        if (!query.isExists) {
            if (bot.client.highway.ssoAddresses.isEmpty())
                bot.client.highway.ssoAddresses += query.uploadServers
            runCatching {
                bot.client.highway.upload(
                    HighwayTransaction(
                        command = 1,
                        ticket = query.uploadKey!!.toByteArray().toUByteArray(),
                        body = data.toUByteArray()
                    )
                )
            }.recoverCatching {
                val groupQuery = queryGroupImage(md5, data.size)
                if (!groupQuery.isExists) {
                    bot.client.highway.upload(
                        HighwayTransaction(
                            command = 2,
                            ticket = groupQuery.uploadKey!!.toByteArray().toUByteArray(),
                            body = data.toUByteArray()
                        )
                    )
                }
            }.getOrThrow()
            return uploadImage(data, depth = depth + 1)
        } else {
            return query
        }
    }

    suspend fun queryImage(md5: HashCode, size: Int) = (bot.client.sendAndWait(
        QueryFriendImageRequest.create(
            bot.client,
            target = id,
            md5 = md5,
            fileSize = size
        )
    ) as QueryFriendImageResponse).result

    suspend fun queryGroupImage(md5: HashCode, size: Int) = (bot.client.sendAndWait(
        UploadGroupPictureRequest.create(
            bot.client,
            groupCode = id,
            md5 = md5,
            fileSize = size
        )
    ) as UploadGroupPictureResponse).result

}