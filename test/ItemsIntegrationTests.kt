package io.devolan

import io.devolan.TokenFactory.Companion.tokenWithAdminRole
import io.devolan.TokenFactory.Companion.tokenWithUserRole
import io.devolan.ktalog.api
import io.devolan.ktalog.items.Drops
import io.devolan.ktalog.items.Items
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ItemsIntegrationTests {

    private val testApplication: Application.() -> Unit = {
        (environment.config as MapApplicationConfig).also{
            it.put("auth.jwt.jwkIssuer", "https://dev-1870027.okta.com/oauth2/default")
            it.put("auth.jwt.audience", "api://default")
        }
        api()
        cleanAndInsertData()
    }

    private val requestWithAdminRole: TestApplicationRequest.() -> Unit = {
        addHeader(HttpHeaders.Authorization, "Bearer ".plus(tokenWithAdminRole()))
    }

    private val requestWithUserRole: TestApplicationRequest.() -> Unit = {
        addHeader(HttpHeaders.Authorization, "Bearer ".plus(tokenWithUserRole()))
    }

    @Test
    fun get_all_items() {

        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Get, "/items", requestWithAdminRole).apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())
            }
        }
    }

    @Test
    fun get_all_items_when_wrong_role() {

        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Get, "/items", requestWithUserRole).apply {
                assertEquals(HttpStatusCode.Forbidden, response.status())
            }
        }
    }

    @Test
    fun get_one_item_by_id() {

        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Get, "/items/a3cba424-8f1f-4b3b-8fba-41bd5d011323", requestWithAdminRole).apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())
            }
        }
    }

    @Test
    fun delete_one_item_malformed_uuid() {

        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Delete, "/items/malformed-uuid", requestWithAdminRole).apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNull(response.content)
            }
        }
    }

    @Test
    fun delete_one_item_not_exist() {

        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Delete, "/items/a3cba424-8f1f-4b3b-8fba-000000000000", requestWithAdminRole).apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun delete_one_item() {

        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Delete, "/items/a3cba424-8f1f-4b3b-8fba-41bd5d011323", requestWithAdminRole).apply {
                assertEquals(HttpStatusCode.NoContent, response.status())
            }
            handleRequest(HttpMethod.Get, "/items/a3cba424-8f1f-4b3b-8fba-41bd5d011323", requestWithAdminRole).apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun delete_one_item_not_authorized() {

        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Delete, "/items/a3cba424-8f1f-4b3b-8fba-000000000000").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    private fun cleanAndInsertData() {
        transaction {

            SchemaUtils.drop(Drops, Items)
            SchemaUtils.create(Items, Drops)

            val itemId = Items.insert {
                it[id] = UUID.fromString("a3cba424-8f1f-4b3b-8fba-41bd5d011323")
                it[description] = "Une clé bleue"
                it[comment] = "c'était un clé qui servait à ouvrir le camion"
            } get Items.id

            Drops.insert {
                it[id] = UUID.fromString("714cc097-5f19-49f8-8cf8-520f8967475e")
                it[content] = "à donner sur FB"
                it[Drops.itemId] = itemId
            }

            Drops.insert {
                it[id] = UUID.fromString("11f80a9f-c13f-415a-9ef3-e5829077cbfc")
                it[content] = "à donner sur Seconde main"
                it[Drops.itemId] = itemId
            }
        }
    }
}
