package io.devolan.ktalog.items

import io.devolan.ktalog.config.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.*

class ItemService {

    suspend fun getItems(): List<Item> = dbQuery {
        Items.leftJoin(Drops).selectAll().toItems()
    }

    suspend fun getItemById(id: UUID): Item? = dbQuery {
        Items.leftJoin(Drops).select { Items.id eq id }
            .toItems()
            .firstOrNull()
    }

    suspend fun insert(item: NewItem) = dbQuery {
        val itemId = Items.insert {
            it[id] = UUID.randomUUID()
            it[description] = item.description
            it[comment] = item.comment
        } get Items.id

        item.drops.forEach { s ->
            Drops.insert {
                it[id] = UUID.randomUUID()
                it[content] = s
                it[Drops.itemId] = itemId
            }
        }
    }

}
