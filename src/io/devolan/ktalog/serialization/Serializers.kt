package io.devolan.ktalog.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor get() = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    private val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(encoder: Encoder, value: LocalDate) {
        val s = value.format(dateFormat)
        encoder.encodeString(s)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val s = decoder.decodeString()
        return LocalDate.parse(s, dateFormat)
    }
}

@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor get() = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(output: Encoder, obj: UUID) {
        output.encodeString(obj.toString())
    }

    override fun deserialize(input: Decoder): UUID {
        return UUID.fromString(input.decodeString())
    }
}
