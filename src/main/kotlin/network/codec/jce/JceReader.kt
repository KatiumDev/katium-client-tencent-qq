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
package katium.client.qq.network.codec.jce

import io.netty.buffer.ByteBuf
import katium.core.util.netty.readUByte
import java.nio.charset.Charset

fun <T> ByteBuf.readJceTag(): Pair<UByte, T> {
    val (type, tag) = readJceHead()
    @Suppress("UNCHECKED_CAST")
    return tag to readJceTag(type) as T
}

fun ByteBuf.readJceTag(type: UByte): Any {
    return when (type) {
        JceConstants.TYPE_BYTE -> readJceByte()
        JceConstants.TYPE_SHORT -> readJceShort()
        JceConstants.TYPE_INT -> readJceInt()
        JceConstants.TYPE_LONG -> readJceLong()
        JceConstants.TYPE_FLOAT -> readJceFloat()
        JceConstants.TYPE_DOUBLE -> readJceDouble()
        JceConstants.TYPE_STRING1 -> readJceString1()
        JceConstants.TYPE_STRING4 -> readJceString4()
        JceConstants.TYPE_MAP -> readJceMap()
        JceConstants.TYPE_LIST -> readJceList()
        JceConstants.TYPE_STRUCT_BEGIN -> readJceStruct()
        JceConstants.TYPE_ZERO -> 0
        JceConstants.TYPE_SIMPLE_LIST -> readJceSimpleList()
        else -> throw UnsupportedOperationException("$type at ${readerIndex()}")
    }
}

fun <T> ByteBuf.readJceTagValue(): T = readJceTag<T>().second

fun ByteBuf.readJceHead(): Pair<UByte, UByte> {
    val firstByte = readUByte()
    val type = firstByte and 0x0fu
    var tag = ((firstByte.toInt() and 0xf0) ushr 4).toUByte()
    if (tag == 0xf.toUByte()) {
        tag = readUByte()
    }
    return type to tag
}

fun ByteBuf.readJceByte() = readByte()
fun ByteBuf.readJceShort() = readShort()
fun ByteBuf.readJceInt() = readInt()
fun ByteBuf.readJceLong() = readLong()
fun ByteBuf.readJceFloat() = readFloat()
fun ByteBuf.readJceDouble() = readDouble()

fun ByteBuf.readJceString1(charset: Charset = JceConstants.DEFAULT_CHARSET): String {
    val buffer = ByteArray(readByte().toInt())
    readBytes(buffer)
    return String(buffer, charset)
}

fun ByteBuf.readJceString4(charset: Charset = JceConstants.DEFAULT_CHARSET): String {
    val buffer = ByteArray(readInt())
    readBytes(buffer)
    return String(buffer, charset)
}

fun ByteBuf.readJceMap(): MutableMap<*, *> {
    val map = mutableMapOf<Any, Any>()
    val size = readJceTagValue<Number>().toInt()
    if (size < 0) {
        throw IllegalArgumentException("Attempt to read JCE map tag with negative size $size")
    }
    for (i in 0 until size) {
        map[readJceTagValue()] = readJceTagValue()
    }
    return map
}

fun ByteBuf.readJceList(): MutableList<*> {
    val list = mutableListOf<Any>()
    val size = readJceTagValue<Number>().toInt()
    if (size < 0) {
        throw IllegalArgumentException("Attempt to read JCE list tag with negative size $size")
    }
    for (i in 0 until size) {
        list.add(readJceTagValue())
    }
    return list
}

fun ByteBuf.readJceStruct(): SimpleJceStruct {
    val tags: MutableMap<UByte, Any> = mutableMapOf()
    while (true) {
        if(!isReadable) {
            break
        }
        val (type, tag) = readJceHead()
        if (type == JceConstants.TYPE_STRUCT_END) {
            break
        }
        tags[tag] = readJceTag(type)
    }
    return SimpleJceStruct(tags)
}

fun ByteBuf.readJceSimpleList(): ByteBuf {
    if (readJceHead().first != 0u.toUByte()) {
        throw IllegalArgumentException("Only byte simple list supported")
    }
    val size = readJceTagValue<Number>().toInt()
    if (size < 0) {
        throw IllegalArgumentException("Attempt to read JCE simple list tag with negative size $size")
    }
    return readBytes(size)
}
