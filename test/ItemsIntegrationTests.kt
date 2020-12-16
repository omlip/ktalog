package io.devolan

import io.devolan.TokenFactory.Companion.tokenWithAdminRole
import io.devolan.TokenFactory.Companion.tokenWithUserRole
import io.devolan.ktalog.items.Drops
import io.devolan.ktalog.items.Items
import io.devolan.ktalog.module
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

class ItemsIntegrationTests {

    private val headerName = "Authorization"
    private val bearerPrefix = "Bearer "

    private val testConfig: MapApplicationConfig.() -> Unit = {
        put("auth.jwt.jwkIssuer", "https://dev-1870027.okta.com/oauth2/default")
        put("auth.jwt.audience", "api://default")
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

    @Test
    fun `get_all_items`() {
        withTestApplication({
            (environment.config as MapApplicationConfig).also(testConfig)
            module()
            cleanAndInsertData()
        }) {
            handleRequest(HttpMethod.Get, "/items") {
                addHeader(headerName, bearerPrefix.plus(tokenWithAdminRole()))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())
            }
        }
    }

    @Test
    fun get_all_items_when_not_authorized() {
        withTestApplication({
            (environment.config as MapApplicationConfig).also(testConfig)
            module()
            cleanAndInsertData()
        }) {
            handleRequest(HttpMethod.Get, "/items") {
                addHeader("Authorization", "Bearer ".plus(tokenWithUserRole()))
            }.apply {
                assertEquals(HttpStatusCode.Forbidden, response.status())
            }
        }
    }

    @Test
    fun `get_one_item_by_id`() {

        withTestApplication({
            (environment.config as MapApplicationConfig).also(testConfig)
            module()
            cleanAndInsertData()
        }) {

            handleRequest(HttpMethod.Get, "/items/a3cba424-8f1f-4b3b-8fba-41bd5d011323") {
                addHeader(headerName, bearerPrefix.plus(tokenWithAdminRole()))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), response.contentType())
            }
        }
    }
}
