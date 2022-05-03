package katium.client.qq.network.codec.jce

import kotlin.reflect.KClass

interface JceStruct {

    val tags: Map<UByte, Any>

}

open class SimpleJceStruct(override val tags: Map<UByte, Any>) : JceStruct {

    fun <T : SimpleJceStruct> to(type: KClass<T>): T =
        (type.constructors
            .find { it.parameters.size == 1 && it.parameters[0].type.classifier == Map::class }
            ?: throw UnsupportedOperationException("Unable to find constructor for $type"))
            .call(tags)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleJceStruct) return false
        if (tags != other.tags) return false
        return true
    }

    override fun hashCode() = tags.hashCode()

    override fun toString() = "SimpleJceStruct($tags)"

}
