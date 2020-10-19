package io.devolan

import io.devolan.ktalog.module
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {

    @Test
    fun testHealth() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("ktor.environment", "local")
            }
            module()
        }) {

            handleRequest(HttpMethod.Get, "/health").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("UP", response.content)
            }
        }
    }

    @Test
    fun testItems() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("ktor.environment", "local")
            }
            module()
        }) {

            handleRequest(HttpMethod.Get, "/items").apply {
                assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun testItemsId() {
        withTestApplication({

            (environment.config as MapApplicationConfig).apply {
                put("ktor.environment", "local")
            }
            module()

        }) {

            handleRequest(HttpMethod.Get, "/items/a3cba424-8f1f-4b3b-8fba-41bd5d011323").apply {
                assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
        }


    }
}
