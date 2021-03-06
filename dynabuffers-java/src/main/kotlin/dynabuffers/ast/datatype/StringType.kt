package dynabuffers.ast.datatype

import dynabuffers.api.IRegistry
import dynabuffers.api.ISerializable
import dynabuffers.api.IType
import java.nio.ByteBuffer
import java.nio.charset.Charset

class StringType(private val options: StringTypeOptions) : IType, ISerializable {

    override fun size(value: Any, registry: IRegistry) = 2 + str(value).toByteArray(options.charset).size

    override fun serialize(value: Any, buffer: ByteBuffer, registry: IRegistry) {
        val encoded = str(value).toByteArray(options.charset)
        buffer.putShort(encoded.size.toShort())
        buffer.put(encoded)
    }

    override fun deserialize(buffer: ByteBuffer, registry: IRegistry): Any {
        val length = buffer.short
        val array = ByteArray(length.toInt())
        buffer.get(array)

        return String(array)
    }

    private fun str(obj: Any) = obj.toString()

    data class StringTypeOptions(val charset: Charset)

}
