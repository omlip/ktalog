package io.devolan.ktalog.items

import io.devolan.ktalog.serialization.LocalDateSerializer
import io.devolan.ktalog.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.date
import java.time.LocalDate
import java.util.*

data class NewItem(
    val description: String,
    val comment: String? = null,
    val drops: List<String> = emptyList()
)

@Serializable
data class Item(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val description: String,
    val comment: String?,
    val drops: ArrayList<Drop>?
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

object Items : Table("items") {
    val id = uuid("id")
    val comment = varchar("comment", 300).nullable()
    val description = varchar("description", 300)
    override val primaryKey = PrimaryKey(id) // name is optional here
}

object Drops : Table("drops") {
    val id = uuid("id")
    val content = varchar("content", 500)
    val fromDate = date("date").default(LocalDate.now())
    val endDate = date("date").nullable()
    val itemId = uuid("item_id") references Items.id
    override val primaryKey = PrimaryKey(id)
}

fun Query.toItems(): List<Item> {
    val results = ArrayList<Item>()
    toList().forEach {row ->

        val ctx = ArrayList<Drop>()

        if (row[Drops.id] != null) ctx.add(
            Drop(
                row[Drops.id],
                row[Drops.content],
                row[Drops.fromDate],
                row[Drops.endDate]
            )
        )

        results.find { it.id == row[Items.id] }?.drops?.addAll(ctx) ?: results.add(
            Item(
                row[Items.id],
                row[Items.description],
                row[Items.comment],
                ctx
            )
        )
    }
    return results
}
