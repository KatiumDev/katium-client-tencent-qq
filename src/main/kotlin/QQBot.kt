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
package katium.client.qq

import katium.client.qq.network.QQClient
import katium.client.qq.user.QQUser
import katium.core.Bot
import katium.core.chat.LocalChatID
import katium.core.review.ReviewMessage
import katium.core.user.Contact
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class QQBot(config: Map<String, String>) : Bot(QQBotPlatform, QQLocalChatID(config["qq.user.id"]!!.toLong()), config) {

    val uin = selfID.asQQ.uin
    val client: QQClient = QQClient(this)

    var loopContinuation: Continuation<Unit>? = null
    override val loopJob = launch(start = CoroutineStart.LAZY) {
        client.connect()
        suspendCoroutine<Unit> {
            loopContinuation = it
        }
        client.close()
    }

    override fun stop() {
        loopContinuation!!.resume(Unit)
    }

    override val allContacts: Set<Contact>
        get() = client.getFriendsSync().values.toSet()

    override val isConnected by client::isConnected
    override val isOnline by client::isOnline

    override suspend fun getGroup(id: LocalChatID) = getGroup(id.asQQ.uin)
    override suspend fun getUser(id: LocalChatID) = getUser(id.asQQ.uin)

    fun getUserSync(id: Long) = runBlocking(coroutineContext) { getUser(id) }
    fun getGroupSync(id: Long) = runBlocking(coroutineContext) { getGroup(id) }

    suspend fun getGroup(id: Long) = client.getGroups()[id]

    suspend fun getUser(id: Long) = client.getFriends()[id]?.asUser ?: QQUser(this, id, "Unknown", false)

    override val reviewMessages: Set<ReviewMessage> by client::reviewMessages

    val allowSlider = (config["qq.allow_slider"] ?: "true").toBoolean()

}
