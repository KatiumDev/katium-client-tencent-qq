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
@file:Suppress("FunctionName")
package katium.client.qq.message.builder

import katium.client.qq.message.content.QQServiceMessage

fun QQUrlShareMessage(url: String, title: String, description: String, image: String) = QQServiceMessage(
    id = 1,
    type = QQServiceMessage.Type.URL_SHARE,
    content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><msg templateID=\"12345\" action=\"web\" brief=\"[分享] $title\" serviceID=\"1\" url=\"$url\"><item layout=\"2\"><picture cover=\"$image\"/><title>$title</title><summary>$description</summary></item><source/></msg>",
    resourceID = url
)

fun QQXmlMessage(xml: String, id: Int = 60) = QQServiceMessage(
    id = id,
    type = QQServiceMessage.Type.XML,
    content = xml
)

fun QQJsonMessage(json: String, id: Int = 1) = QQServiceMessage(
    id = id,
    type = QQServiceMessage.Type.JSON,
    content = json
)