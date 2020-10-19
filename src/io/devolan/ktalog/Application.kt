package io.devolan.ktalog

import io.devolan.ktalog.config.bindServices
import io.devolan.ktalog.exceptions.AuthenticationException
import io.devolan.ktalog.exceptions.AuthorizationException
import io.devolan.ktalog.items.ItemService
import io.devolan.ktalog.items.itemsRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.*
import org.kodein.di.DI
import org.kodein.di.instance

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module() {

    /**
     *  Dependency injection with Kodein
     */

    var di = DI {
        bindServices("/hikari-$envKind.properties")
    }

    /**
     * Configure ContentNegotiation, here Json
     */
    install(ContentNegotiation){
        json()
    }

    /**
     * Configure CallLogging, logs all the call made to the application
     */
    install(CallLogging)

    /**
     * CORS Configuration
     */
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
        allowCredentials = true
        maxAgeInSeconds = 86400
    }

    /*
    * Configure Authentication
    * */
    install(Authentication) {
        basic("myBasicAuth") {
            realm = "Ktor Server"
            validate {
                if (it.name == "test" && it.password == "password") UserIdPrincipal(it.name) else null
            }
        }
    }

    /*
    * Configure all routes
    * */

    routing {
        get("/health") {
            call.respondText(text = "UP", contentType = ContentType.Text.Plain)
        }

        val itemService: ItemService by di.instance()
        itemsRoute(itemService)

        install(StatusPages) {

            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized, cause.localizedMessage)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden, cause.localizedMessage)
            }
            exception<NotFoundException> { cause ->
                call.respond(HttpStatusCode.NotFound, cause.localizedMessage)
            }
            exception<IllegalArgumentException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.localizedMessage)
            }
        }

        authenticate("myBasicAuth") {
            get("/protected/route/basic") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }
}

val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isLocal get() = envKind == "local"
val Application.isProd get() = envKind == "prod"
