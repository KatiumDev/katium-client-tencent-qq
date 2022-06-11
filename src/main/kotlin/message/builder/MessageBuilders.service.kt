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
@file:Suppress("FunctionName") @file:JvmName("QQServices")

package katium.client.qq.message.builder

import katium.client.qq.message.content.QQService
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.XmlVersion
import org.redundent.kotlin.xml.xml

@JvmName("ofUrlShare")
@JvmOverloads
fun QQUrlShare(url: String, title: String, description: String? = null, imageUrl: String? = null) = QQService(
    id = 1,
    type = QQService.Type.URL_SHARE,
    content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<msg templateID=\"12345\" action=\"web\" brief=\"[分享] $title\" serviceID=\"1\" url=\"$url\">" + "<item layout=\"2\">" + (if (imageUrl != null) "<picture cover=\"$imageUrl\"/>" else "") + "<title>$title</title>" + (if (description != null) "<summary>$description</summary>" else "") + "</item><source/></msg>",
    resourceID = url
)

@JvmName("ofXml")
@JvmOverloads
fun QQXml(xml: String, id: Int = 60) = QQService(
    id = id, type = QQService.Type.XML, content = xml
)

@JvmName("ofXml")
@JvmOverloads
fun QQXml(block: Node.() -> Unit, id: Int = 60) = QQService(
    id = id,
    type = QQService.Type.XML,
    content = xml("msg", encoding = "utf-8", version = XmlVersion.V10, init = block).toString(prettyFormat = false)
)

@JvmName("ofJson")
@JvmOverloads
fun QQJson(json: String, id: Int = 1) = QQService(
    id = id, type = QQService.Type.JSON, content = json
)