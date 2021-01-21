package io.devolan.ktalog.monitoring

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.health() {
    get("/health") {
        call.respondText(text = "UP", contentType = ContentType.Text.Plain)
    }
}
