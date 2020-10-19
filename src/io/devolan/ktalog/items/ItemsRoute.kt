package io.devolan.ktalog.items

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.cio.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import java.util.*

fun Route.itemsRoute(itemService: ItemService) {

    route("/items") {

        get {
            val items: List<Item> = itemService.getItems()
            call.respond(items)
        }

        post {
            val request = call.receive<Request>()

            val newItem = NewItem(request.description, request.comment, listOf(request.context))

            itemService.insert(newItem)
            call.respond(HttpStatusCode.Created)
        }

        get("/{id}") {
            val id = UUID.fromString(call.parameters["id"].toString())
            val item = itemService.getItemById(id) ?: throw NotFoundException()

            call.respond(item)
        }

    }

}

data class Request (val description : String,
                    val comment: String,
                    val context: String
)
