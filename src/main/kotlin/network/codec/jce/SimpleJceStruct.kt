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
import io.netty.buffer.ByteBufAllocator
import katium.core.util.netty.EmptyByteBuf
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

open class SimpleJceStruct(override val tags: MutableMap<UByte, Any>) : JceStruct, AutoCloseable {

    constructor() : this(mutableMapOf())

    constructor(other: SimpleJceStruct) : this(other.tags.toMutableMap())

    operator fun get(tag: UByte) = tags[tag]
    operator fun set(tag: UByte, value: Any) {
        tags[tag] = value
    }

    protected fun <T : Any> field(tag: UByte, defaultValue: () -> T): Delegation<T> {
        tags.computeIfAbsent(tag) { defaultValue() }
        return Delegation(tag)
    }

    protected fun <T : Any> field(tag: UByte, defaultValue: T) = field(tag) { defaultValue }
    protected fun string(tag: UByte, defaultValue: String = ""): Delegation<String> = field(tag, defaultValue)
    protected fun <K, V> map(tag: UByte): Delegation<MutableMap<K, V>> = field(tag) { mutableMapOf() }
    protected fun <E> list(tag: UByte): Delegation<MutableList<E>> = field(tag) { mutableListOf() }
    protected fun <E> set(tag: UByte): Delegation<MutableSet<E>> = field(tag) { mutableSetOf() }
    protected fun byteBuf(tag: UByte): Delegation<ByteBuf> = field(tag) { EmptyByteBuf }
    protected fun <T : SimpleJceStruct> struct(tag: UByte, type: KClass<T>): StructDelegation<T> {
        tags.computeIfAbsent(tag) { type.constructors.find { it.parameters.isEmpty() }!!.call() }
        return StructDelegation(
            tag, type, type.constructors
                .find { it.parameters.size == 1 && it.parameters[0].type.jvmErasure == SimpleJceStruct::class }!!::call
        )
    }

    protected fun <T : Number> number(tag: UByte, type: KClass<T>, defaultValue: Number = 0): NumberDelegation<T> {
        tags.putIfAbsent(tag, defaultValue)
        return NumberDelegation(tag, type)
    }

    protected inline fun <reified T : Number> number(tag: UByte, defaultValue: Number = 0) =
        number(tag, T::class, defaultValue)

    fun dump(release: Boolean = true): ByteBuf {
        val data = ByteBufAllocator.DEFAULT.heapBuffer().writeJcePureStruct(this)
        if (release) release()
        return data
    }

    open fun release() {}
    operator fun unaryMinus() = release()
    override fun close() = release()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleJceStruct) return false
        if (tags != other.tags) return false
        return true
    }

    override fun hashCode() = tags.hashCode()

    override fun toString() = tags.toString()

    open inner class Delegation<T : Any>(val tag: UByte) {

        @Suppress("UNCHECKED_CAST")
        open operator fun getValue(thisRef: SimpleJceStruct?, property: KProperty<*>?): T = tags[tag] as T

        open operator fun setValue(thisRef: SimpleJceStruct?, property: KProperty<*>?, value: T) {
            tags[tag] = value
        }

    }

    inner class NumberDelegation<T : Number>(tag: UByte, type: KClass<T>) :
        Delegation<T>(tag) {

        @Suppress("UNCHECKED_CAST")
        val castFunction: (Number) -> T = when (type) {
            Byte::class -> { number -> number.toByte() as T }
            UByte::class -> { number -> number.toByte().toUByte() as T }
            Short::class -> { number -> number.toShort() as T }
            UShort::class -> { number -> number.toShort().toUShort() as T }
            Int::class -> { number -> number.toInt() as T }
            UInt::class -> { number -> number.toInt().toUInt() as T }
            Long::class -> { number -> number.toLong() as T }
            ULong::class -> { number -> number.toLong().toULong() as T }
            else -> throw IllegalArgumentException("Unsupported number type: $type")
        }

        @Suppress("UNCHECKED_CAST")
        override operator fun getValue(thisRef: SimpleJceStruct?, property: KProperty<*>?): T =
            castFunction(super.getValue(thisRef, property) as Number)

    }

    inner class StructDelegation<T : SimpleJceStruct>(
        tag: UByte,
        val type: KClass<T>,
        val castFunction: (SimpleJceStruct) -> T
    ) : Delegation<T>(tag) {

        override operator fun getValue(thisRef: SimpleJceStruct?, property: KProperty<*>?): T {
            val value = super.getValue(thisRef, property)
            return if (type.isInstance(value)) {
                value
            } else {
                val castedValue = castFunction(value)
                super.setValue(thisRef, property, castedValue)
                castedValue
            }
        }

    }

}
