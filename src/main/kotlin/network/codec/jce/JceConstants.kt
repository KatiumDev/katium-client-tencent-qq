/*
 * Katium Client Tencent QQ: Tencent QQ protocol implementation for Katium
 * Copyright (C) 2022  Katium Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package katium.client.qq.network.codec.jce

import java.nio.charset.Charset

object JceConstants {

    const val TYPE_BYTE: UByte = 0u
    const val TYPE_SHORT: UByte = 1u
    const val TYPE_INT: UByte = 2u
    const val TYPE_LONG: UByte = 3u
    const val TYPE_FLOAT: UByte = 4u
    const val TYPE_DOUBLE: UByte = 5u
    const val TYPE_STRING1: UByte = 6u
    const val TYPE_STRING4: UByte = 7u
    const val TYPE_MAP: UByte = 8u
    const val TYPE_LIST: UByte = 9u
    const val TYPE_STRUCT_BEGIN: UByte = 10u
    const val TYPE_STRUCT_END: UByte = 11u
    const val TYPE_ZERO: UByte = 12u
    const val TYPE_SIMPLE_LIST: UByte = 13u

    const val TAG_MAP_KEY: UByte = 0u
    const val TAG_MAP_VALUE: UByte = 1u
    const val TAG_LIST_ELEMENT: UByte = 0u
    const val TAG_BYTES: UByte = 0u
    const val TAG_LENGTH: UByte = 0u
    const val TAG_STRUCT_END: UByte = 0u

    @JvmField
    val DEFAULT_CHARSET: Charset = Charset.forName("GBK")

}