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

import katium.client.qq.group.QQGroup
import katium.client.qq.network.QQClient
import katium.client.qq.user.QQUser
import katium.core.Bot
import katium.core.chat.LocalChatID
import katium.core.review.ReviewMessage
import katium.core.user.Contact
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class QQBot(config: Map<String, String>) : Bot(QQBotPlatform, QQLocalChatID(config["qq.user.id"]!!.toLong()), config) {

    val uin = selfID.asQQ.uin
    val client: QQClient = QQClient(this)

    var loopContinuation: CancellableContinuation<Unit>? = null
    override val loopJob = launch(start = CoroutineStart.LAZY) {
        client.connect()
        suspendCancellableCoroutine {
            loopContinuation = it
        }
    }

    override val allContacts: Set<Contact>
        get() = TODO("Not yet implemented")

    override val isConnected by client::isConnected
    override val isOnline by client::isOnline

    override fun getGroup(id: LocalChatID) = getGroup(id.asQQ.uin)
    override fun getUser(id: LocalChatID) = getUser(id.asQQ.uin)

    fun getGroup(id: Long): QQGroup {
        // @TODO: Cache groups
        return QQGroup(this, id)
    }

    fun getUser(id: Long): QQUser {
        // @TODO: Cache users
        return QQUser(this, id)
    }

    override val reviewMessages: Set<ReviewMessage> by client::reviewMessages

    val allowSlider = (config["qq.allow_slider"] ?: "true").toBoolean()

}
