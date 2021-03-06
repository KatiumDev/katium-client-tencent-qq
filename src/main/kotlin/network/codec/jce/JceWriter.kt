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
import katium.core.util.netty.writeUByte
import java.nio.charset.Charset

fun ByteBuf.writeJceTag(tag: UByte, value: Any): ByteBuf {
    when (value) {
        is Byte -> writeJceByte(tag, value)
        is Short -> writeJceShort(tag, value)
        is Int -> writeJceInt(tag, value)
        is Long -> writeJceLong(tag, value)
        is Float -> writeJceFloat(tag, value)
        is Double -> writeJceDouble(tag, value)
        is String -> writeJceString(tag, value)
        is Map<*, *> -> writeJceMap(tag, value)
        is Collection<*> -> writeJceList(tag, value)
        is ByteArray -> writeJceSimpleList(tag, value)
        is ByteBuf -> writeJceSimpleList(tag, value)
        is JceStruct -> writeJceStruct(tag, value)
        else -> throw UnsupportedOperationException("Unsupported Jce tag type: ${value.javaClass}")
    }
    return this
}

fun ByteBuf.writeJceHead(type: UByte, tag: UByte) {
    if (type > 0xfu || type < 0u) {
        throw IllegalArgumentException("Attempting to write overflow Jce type $type")
    }
    if (tag in 0u..0xeu) {
        writeUByte((tag.toInt() shl 4).toUByte() or type)
    } else {
        writeUByte(0xf0u.toUByte() or type)
        writeUByte(tag)
    }
}

fun ByteBuf.writeJceByte(tag: UByte, value: Byte): ByteBuf {
    if (value == 0.toByte()) {
        writeJceHead(JceConstants.TYPE_ZERO, tag)
    } else {
        writeJceHead(JceConstants.TYPE_BYTE, tag)
        writeByte(value.toInt())
    }
    return this
}

fun ByteBuf.writeJceShort(tag: UByte, value: Short): ByteBuf {
    if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
        // cast to byte
        writeJceByte(tag, value.toByte())
    } else {
        writeJceHead(JceConstants.TYPE_SHORT, tag)
        writeShort(value.toInt())
    }
    return this
}

fun ByteBuf.writeJceInt(tag: UByte, value: Int): ByteBuf {
    if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
        // cast to short
        writeJceShort(tag, value.toShort())
    } else {
        writeJceHead(JceConstants.TYPE_INT, tag)
        writeInt(value)
    }
    return this
}

fun ByteBuf.writeJceLong(tag: UByte, value: Long): ByteBuf {
    if (value >= Int.MIN_VALUE && value <= Int.MAX_VALUE) {
        // cast to byte
        writeJceInt(tag, value.toInt())
    } else {
        writeJceHead(JceConstants.TYPE_LONG, tag)
        writeLong(value)
    }
    return this
}

fun ByteBuf.writeJceFloat(tag: UByte, value: Float): ByteBuf {
    writeJceHead(JceConstants.TYPE_FLOAT, tag)
    writeFloat(value)
    return this
}

fun ByteBuf.writeJceDouble(tag: UByte, value: Double): ByteBuf {
    writeJceHead(JceConstants.TYPE_DOUBLE, tag)
    writeDouble(value)
    return this
}

fun ByteBuf.writeJceString(tag: UByte, value: String, charset: Charset = JceConstants.DEFAULT_CHARSET): ByteBuf {
    val bytes = value.toByteArray(charset)
    if (bytes.size <= 0xff) {
        // string1
        writeJceHead(JceConstants.TYPE_STRING1, tag)
        writeByte(bytes.size)
    } else {
        // string4
        writeJceHead(JceConstants.TYPE_STRING4, tag)
        writeInt(bytes.size)
    }
    writeBytes(bytes)
    return this
}

fun ByteBuf.writeJceMap(tag: UByte, value: Map<*, *>): ByteBuf {
    writeJceHead(JceConstants.TYPE_MAP, tag)
    writeJceInt(JceConstants.TAG_LENGTH, value.size)
    value.forEach { (key, value) ->
        writeJceTag(JceConstants.TAG_MAP_KEY, key!!)
        writeJceTag(JceConstants.TAG_MAP_VALUE, value!!)
    }
    return this
}

fun ByteBuf.writeJceList(tag: UByte, value: Collection<*>): ByteBuf {
    if (value.isNotEmpty() && value.all { it is Byte }) {
        // cast to simple list
        val buffer = ByteArray(value.size)
        value.forEachIndexed { index, byte -> buffer[index] = byte as Byte }
        writeJceSimpleList(tag, buffer)
    } else {
        writeJceHead(JceConstants.TYPE_LIST, tag)
        writeJceInt(JceConstants.TAG_LENGTH, value.size)
        value.forEach { writeJceTag(JceConstants.TAG_LIST_ELEMENT, it!!) }
    }
    return this
}

fun ByteBuf.writeJceSimpleList(tag: UByte, value: ByteBuf): ByteBuf {
    val buffer = ByteArray(value.readableBytes())
    value.readBytes(buffer)
    return writeJceSimpleList(tag, buffer)
}

fun ByteBuf.writeJceSimpleList(tag: UByte, value: ByteArray): ByteBuf {
    writeJceHead(JceConstants.TYPE_SIMPLE_LIST, tag)
    writeJceHead(JceConstants.TYPE_BYTE, JceConstants.TAG_BYTES)
    writeJceInt(0u, value.size)
    writeBytes(value)
    return this
}

fun ByteBuf.writeJceStruct(tag: UByte, value: JceStruct): ByteBuf {
    writeJceHead(JceConstants.TYPE_STRUCT_BEGIN, tag)
    writeJcePureStruct(value)
    writeJceHead(JceConstants.TYPE_STRUCT_END, JceConstants.TAG_STRUCT_END)
    return this
}

fun ByteBuf.writeJcePureStruct(value: JceStruct): ByteBuf {
    value.tags.entries
        .sortedBy(Map.Entry<UByte, Any>::key)
        .forEach { (tag, value) ->
            writeJceTag(tag, value)
        }
    return this
}
