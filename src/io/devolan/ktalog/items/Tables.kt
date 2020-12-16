package io.devolan.ktalog.items

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.date
import java.time.LocalDate

object Items : Table("items") {
    val id = uuid("id")
    val comment = varchar("comment", 300).nullable()
    val description = varchar("description", 300)
    override val primaryKey = PrimaryKey(id) // name is optional here
}

object Drops : Table("drops") {
    val id = uuid("id")
    val content = varchar("content", 500)
    val fromDate = date("fromdate").default(LocalDate.now())
    val endDate = date("enddate").nullable()
    val itemId = uuid("item_id") references Items.id
    override val primaryKey = PrimaryKey(id)
}
