package io.devolan

import io.devolan.ktalog.module
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthTest {

    private val testConfig: MapApplicationConfig.() -> Unit = {
        put("auth.jwt.jwkIssuer", "https://dev-1870027.okta.com/oauth2/default")
        put("auth.jwt.audience", "api://default")
    }

    @Test
    fun `test_health_endpoint_is_ip`(){
        withTestApplication({
            (environment.config as MapApplicationConfig).also(testConfig)
            module()
        }) {
            handleRequest(HttpMethod.Get, "/health").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("UP", response.content)
            }
        }
    }
}
