package io.devolan.ktalog

import io.devolan.ktalog.items.Drop
import io.devolan.ktalog.items.Drops
import io.devolan.ktalog.items.Item
import io.devolan.ktalog.items.Items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseUtil {

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

}

fun Query.toItems(): List<Item> {
    val results = ArrayList<Item>()
    toList().forEach {row ->

        val drops = ArrayList<Drop>()

        if (row[Drops.id] != null) drops.add(
            Drop(
                row[Drops.id],
                row[Drops.content],
                row[Drops.fromDate],
                row[Drops.endDate]
            )
        )

        results.find { it.id == row[Items.id] }?.drops?.addAll(drops) ?: results.add(
            Item(
                row[Items.id],
                row[Items.description],
                row[Items.comment],
                drops
            )
        )
    }
    return results
}
