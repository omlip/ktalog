package io.devolan.ktalog.items

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun Route.itemsRoute(itemService: ItemService) {

    route("/items") {

        get { call.respond(itemService.getItems()) }

        post {
            val request = call.receive<Request>()

            val newItem = NewItem(request.description, request.comment, listOf(request.context))

            itemService.insert(newItem)
            call.respond(HttpStatusCode.Created)
        }

        get("/{id}") {
            val id = UUID.fromString(call.parameters["id"].toString())
            when (val i = itemService.getItemById(id)) {
                null -> throw NotFoundException()
                else-> call.respond(i)
            }
        }

    }

}

data class NewItem(
    val description: String,
    val comment: String? = null,
    val drops: List<String> = emptyList()
)

data class Request(
    val description: String,
    val comment: String,
    val context: String
)
