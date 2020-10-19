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
    val contexts: List<String> = emptyList()
)

@Serializable
data class Item(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val comment: String?,
    val description: String,
    val contexts: ArrayList<Context>
)

@Serializable
data class Context(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val content: String,
    @Serializable(with = LocalDateSerializer::class)
    val createDate: LocalDate
)

object Items : Table() {
    val id = uuid("id")
    val comment = varchar("comment", 300).nullable()
    val description = varchar("description", 300)
    override val primaryKey = PrimaryKey(id) // name is optional here
}

object Contexts : Table() {
    val id = uuid("id")
    val content = varchar("content", 500)
    val createDate = date("date").default(LocalDate.now())
    val itemId = uuid("item_id") references Items.id
    override val primaryKey = PrimaryKey(id)
}

fun Query.toItems(): List<Item> {
    val results = ArrayList<Item>()
    toList().forEach {row ->

        val ctx = ArrayList<Context>()

        if (row[Contexts.id] != null) ctx.add(
            Context(
                row[Contexts.id],
                row[Contexts.content],
                row[Contexts.createDate]
            )
        )

        results.find { it.id == row[Items.id] }?.contexts?.addAll(ctx) ?: results.add(
            Item(
                row[Items.id],
                row[Items.comment],
                row[Items.description],
                ctx
            )
        )
    }
    return results
}
