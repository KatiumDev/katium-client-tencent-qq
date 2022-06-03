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
package katium.client.qq.message.content

import katium.core.message.content.MessageContent

class QQServiceMessage(
    val id: Int,
    val type: Type,
    val content: String,
    val resourceID: String = "",
): MessageContent() {

    override fun concat(other: MessageContent) = null

    override fun simplify() = null

    override fun toString() = "[QQServiceMessage/$id/$type/$resourceID: $content]"

    enum class Type {

        URL_SHARE, XML, JSON, LONG_MESSAGE, UNKNOWN

    }

}