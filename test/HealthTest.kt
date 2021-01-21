package io.devolan

import io.devolan.ktalog.api
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthTest {

    private val testApplication: Application.() -> Unit = {
        (environment.config as MapApplicationConfig).also{
            it.put("auth.jwt.jwkIssuer", "https://dev-1870027.okta.com/oauth2/default")
            it.put("auth.jwt.audience", "api://default")
        }
        api()
    }

    @Test
    fun `test_health_endpoint_is_ip`(){
        withTestApplication(testApplication) {
            handleRequest(HttpMethod.Get, "/health").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("UP", response.content)
            }
        }
    }
}
