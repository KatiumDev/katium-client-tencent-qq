package katium.client.qq.network.codec.jce

import io.netty.buffer.ByteBufAllocator
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

open class SimpleJceStruct(override val tags: MutableMap<UByte, Any>) : JceStruct {

    constructor() : this(mutableMapOf())

    constructor(other: SimpleJceStruct) : this(other.tags.toMutableMap())

    operator fun get(tag: UByte) = tags[tag]
    operator fun set(tag: UByte, value: Any) {
        tags[tag] = value
    }

    operator fun <T : Any> invoke(tag: UByte, type: KClass<T>, defaultValue: T): Delegation<T> {
        tags.putIfAbsent(tag, defaultValue)
        val delegation = Delegation<T>(tag)
        if (type.isSubclassOf(Number::class)) {
            @Suppress("UNCHECKED_CAST")
            return (delegation + (type as KClass<out Number>)) as Delegation<T>
        }
        return delegation
    }

    fun dump() = ByteBufAllocator.DEFAULT.heapBuffer().writeJcePureStruct(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleJceStruct) return false
        if (tags != other.tags) return false
        return true
    }

    override fun hashCode() = tags.hashCode()

    override fun toString() = "SimpleJceStruct($tags)"

    open inner class Delegation<T : Any>(val tag: UByte) {

        @Suppress("UNCHECKED_CAST")
        open operator fun getValue(thisRef: SimpleJceStruct?, property: KProperty<*>?): T = tags[tag] as T

        open operator fun setValue(thisRef: SimpleJceStruct?, property: KProperty<*>?, value: T) {
            tags[tag] = value
        }

        @Suppress("UNCHECKED_CAST")
        open operator fun <T : Number> plus(type: KClass<T>) = NumberDelegation(tag, type)

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

}
