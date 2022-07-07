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
package katium.client.qq.network.sync

import katium.client.qq.network.QQClient
import katium.client.qq.network.packet.chat.MessageReadReportRequest
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job

class Synchronizer(val client: QQClient) {

    var syncCookie: ByteArray? = null
    var publicAccountCookie: ByteArray? = null

    val friendInitialSync = atomic(true)

    val pullMessagesCache = SynchronizeCache<Triple<Long, Int, Int>>(50)
    fun writePullMessagesCache(uid: Long, sequence: Int, time: Int) =
        pullMessagesCache.write(Triple(uid, sequence, time))

    var readReportJob: Job? = null

    val unreadFriendMessages = mutableListOf<Long>()
    val unreadGroupMessages = mutableListOf<Long>()

    fun recordUnreadFriendMessage(peerUin: Long) {
        unreadFriendMessages += peerUin
    }

    fun recordUnreadFriendMessages(peerUin: Collection<Long>) {
        unreadFriendMessages += peerUin
    }

    fun reportAllFriendRead() {
        if (unreadFriendMessages.isEmpty()) return
        reportFriendRead(unreadFriendMessages)
        unreadFriendMessages.clear()
    }

    fun reportSomeFriendRead() {
        if (unreadFriendMessages.isEmpty()) return
        val shuffled = unreadFriendMessages.shuffled()
        val peerUins = shuffled.take(shuffled.size / 2)
        unreadFriendMessages -= peerUins.toSet()
        reportFriendRead(peerUins)
    }

    fun reportFriendRead(peerUins: Collection<Long>) {
        if (peerUins.isEmpty()) return
        client.logger.info("Reporting ${peerUins.size} friends as read")
        val time = (System.currentTimeMillis() / 1000).toInt()
        client.send(
            MessageReadReportRequest.create(
                client, report = MessageReadReportRequest(
                    friend = MessageReadReportRequest.Friend.Request(
                        syncCookie = syncCookie ?: ByteArray(0), info = peerUins.map {
                            MessageReadReportRequest.Friend.UinPairReadInfo(peerUin = it, lastReadTime = time)
                        }.toSet()
                    )
                )
            )
        )
    }

    fun recordUnreadGroupMessage(groupCode: Long) {
        unreadGroupMessages += groupCode
    }

    suspend fun reportAllGroupRead() {
        if (unreadFriendMessages.isEmpty()) return
        reportGroupRead(unreadGroupMessages)
        unreadGroupMessages.clear()
    }

    suspend fun reportSomeGroupRead() {
        if (unreadGroupMessages.isEmpty()) return
        val shuffled = unreadGroupMessages.shuffled()
        val groups = shuffled.take(shuffled.size / 2)
        unreadGroupMessages -= groups.toSet()
        reportGroupRead(groups)
    }

    suspend fun reportGroupRead(groups: Collection<Long>) {
        if (groups.isEmpty()) return
        client.logger.info("Reporting ${groups.size} groups as read")
        client.send(
            MessageReadReportRequest.create(
                client, report = MessageReadReportRequest(
                    groups = groups.map { groupCode ->
                        MessageReadReportRequest.Group.Request(
                            groupCode = groupCode,
                            lastReadSequence = client.getGroups()[groupCode]!!.lastReadSequence.get()
                        )
                    }.toSet()
                )
            )
        )
    }

}