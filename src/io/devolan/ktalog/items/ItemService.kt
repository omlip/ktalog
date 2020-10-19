package io.devolan.ktalog.items

import io.devolan.ktalog.config.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class ItemService {

    suspend fun getItems(): List<Item> = dbQuery {
        Items.leftJoin(Contexts).selectAll().toItems()
    }

    suspend fun getItemById(id: UUID): Item? = dbQuery {
        Items.leftJoin(Contexts).select { (Items.id eq id) }
            .toItems()
            .firstOrNull()
    }

    suspend fun insert(item: NewItem) = dbQuery {
        val itemId = Items.insert {
            it[id] = UUID.randomUUID()
            it[description] = item.description
            it[comment] = item.comment
        } get Items.id

        item.contexts.forEach { s ->
            Contexts.insert {
                it[id] = UUID.randomUUID()
                it[content] = s
                it[Contexts.itemId] = itemId
            }
        }
    }

}
