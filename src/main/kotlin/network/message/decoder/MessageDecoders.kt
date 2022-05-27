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
package katium.client.qq.network.message.decoder

import katium.client.qq.network.QQClient
import katium.client.qq.network.event.QQMessageDecodersInitializeEvent
import katium.core.util.event.post
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking

class MessageDecoders(val client: QQClient) {

    val decoders: Map<Int, MessageDecoder> by lazy {
        val decoders = mutableMapOf<Int, MessageDecoder>()
        registerBuiltinDecoders(decoders)
        runBlocking(CoroutineName("Initialize Message Decoders")) {
            client.bot.post(QQMessageDecodersInitializeEvent(client, decoders))
        }
        decoders.toMap()
    }

    private fun registerBuiltinDecoders(decoders: MutableMap<Int, MessageDecoder>) {
        /*
        switch msgType {
	case 33:
		return troopAddMemberBroadcastDecoder, otherDecoders
	case 35:
		return troopSystemMessageDecoder, troopSystemMsgDecoders
	case 36:
		return troopSystemMessageDecoder, nonSvcNotifyTroopSystemMsgDecoders
	case 37:
		return troopSystemMessageDecoder, troopSystemMsgDecoders
	case 45:
		return troopSystemMessageDecoder, troopSystemMsgDecoders
	case 46:
		return troopSystemMessageDecoder, troopSystemMsgDecoders
	case 79:
		return privateMessageDecoder, privateMsgDecoders
	case 84:
		return troopSystemMessageDecoder, troopSystemMsgDecoders
	case 85:
		return troopSystemMessageDecoder, nonSvcNotifyTroopSystemMsgDecoders
	case 86:
		return troopSystemMessageDecoder, troopSystemMsgDecoders
	case 87:
		return troopSystemMessageDecoder, troopSystemMsgDecoders
	case 97:
		return privateMessageDecoder, privateMsgDecoders
	case 120:
		return privateMessageDecoder, privateMsgDecoders
	case 132:
		return privateMessageDecoder, privateMsgDecoders
	case 133:
		return privateMessageDecoder, privateMsgDecoders
	case 140:
		return tempSessionDecoder, privateMsgDecoders
	case 141:
		return tempSessionDecoder, privateMsgDecoders
	case 187:
		return systemMessageDecoder, sysMsgDecoders
	case 188:
		return systemMessageDecoder, sysMsgDecoders
	case 189:
		return systemMessageDecoder, sysMsgDecoders
	case 190:
		return systemMessageDecoder, sysMsgDecoders
	case 191:
		return systemMessageDecoder, sysMsgDecoders
	case 208:
		return privatePttDecoder, privateMsgDecoders
	case 529:
		return msgType0x211Decoder, otherDecoders
         */
        decoders[9] = FriendMessageDecoder
        decoders[10] = FriendMessageDecoder
        decoders[31] = FriendMessageDecoder
        decoders[79] = FriendMessageDecoder
        decoders[97] = FriendMessageDecoder
        decoders[120] = FriendMessageDecoder
        decoders[132] = FriendMessageDecoder
        decoders[133] = FriendMessageDecoder
        decoders[166] = FriendMessageDecoder
        decoders[167] = FriendMessageDecoder
    }

    operator fun get(type: Int) = decoders[type]

}