package io.devolan.ktalog.items

import io.devolan.ktalog.serialization.LocalDateSerializer
import io.devolan.ktalog.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

@Serializable
data class Item(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val description: String,
    val comment: String?,
    val drops: ArrayList<Drop> = ArrayList(0)
)

@Serializable
data class Drop(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val content: String,
    @Serializable(with = LocalDateSerializer::class)
    val fromDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate?
)
