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