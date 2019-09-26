package dynabuffers.ast.datatype

import dynabuffers.ast.AbstractAST
import dynabuffers.exception.DynabuffersException
import java.nio.ByteBuffer

class ArrayType(private val options: ArrayTypeOptions) : AbstractAST() {

    override fun size(value: Any, registry: IRegistry): Int {
        return 2 + (size(value) * options.datatype.size(value, registry))
    }

    override fun serialize(value: Any, buffer: ByteBuffer, registry: IRegistry) {
        val list = list(value)
        buffer.putShort(list.size.toShort())
        list.forEach { options.datatype.serialize(it!!, buffer, registry) }
    }

    override fun deserialize(buffer: ByteBuffer, registry: IRegistry): Any {
        val length = buffer.short.toInt()
        return when (options.datatype) {
            is ByteType -> ByteArray(length) { options.datatype.deserialize(buffer, registry) }
            is ShortType -> ShortArray(length) { options.datatype.deserialize(buffer, registry) }
            is IntType -> IntArray(length) { options.datatype.deserialize(buffer, registry) }
            is LongType -> LongArray(length) { options.datatype.deserialize(buffer, registry) }
            is FloatType -> FloatArray(length) { options.datatype.deserialize(buffer, registry) }
            is BooleanType -> BooleanArray(length) { options.datatype.deserialize(buffer, registry) }
            is RefType -> Array(length) { options.datatype.deserialize(buffer, registry) }
            else -> throw DynabuffersException("cannot handle datatype ${options.datatype}")
        }
    }

    private fun list(obj: Any) = when (obj) {
        is Collection<*> -> obj
        is Array<*> -> obj.toList()
        is ByteArray -> obj.toList()
        is IntArray -> obj.toList()
        is ShortArray -> obj.toList()
        is LongArray -> obj.toList()
        is FloatArray -> obj.toList()
        is DoubleArray -> obj.toList()
        else -> throw DynabuffersException("cannot handle value $obj")
    }

    private fun size(obj: Any) = when (obj) {
        is Collection<*> -> obj.size
        is Array<*> -> obj.size
        is ByteArray -> obj.size
        is IntArray -> obj.size
        is ShortArray -> obj.size
        is LongArray -> obj.size
        is FloatArray -> obj.size
        is DoubleArray -> obj.size
        else -> throw DynabuffersException("cannot handle value $obj")
    }

    data class ArrayTypeOptions(val datatype: AbstractAST)

}
