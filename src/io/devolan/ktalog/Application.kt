package io.devolan.ktalog

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.devolan.ktalog.config.AuthorizationException
import io.devolan.ktalog.config.Role
import io.devolan.ktalog.config.RoleBasedAuthorization
import io.devolan.ktalog.config.withRole
import io.devolan.ktalog.exceptions.AuthenticationException
import io.devolan.ktalog.items.ItemService
import io.devolan.ktalog.items.itemsRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.eagerSingleton
import org.kodein.di.instance
import org.kodein.di.singleton
import java.net.URL
import java.util.concurrent.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module() {

    /**
     *  Dependency injection with Kodein
     */
    var di = DI {
        bind<HikariConfig>() with eagerSingleton {
            val config = HikariConfig("/hikari-$envKind.properties")
            config.validate()
            config
        }
        bind<HikariDataSource>() with singleton { HikariDataSource(instance()) }
        bind<Database>() with eagerSingleton {
            val dataSource: HikariDataSource by di.instance()
            val database = Database.connect(dataSource)
            database
        }
        bind<ItemService>() with singleton { ItemService() }
    }

    /**
     * Configure ContentNegotiation, here Json
     */
    install(ContentNegotiation) {
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
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.AccessControlAllowMethods)
        header(HttpHeaders.Authorization)
        host("localhost:4200")

        allowCredentials = false
        maxAgeInSeconds = 86400
    }

    /*
    * Configure Authorization
    * */
    install(RoleBasedAuthorization) {
        getRoles {
            (it as JWTPrincipal).payload.claims["groups"]!!.asList(Role::class.java).toSet()
        }
    }

    /*
    * Configure Authentication
    * */
    install(Authentication) {
        basic("myBasicAuth") {
            validate {
                if (it.name == "test" && it.password == "password") UserIdPrincipal(it.name) else null
            }
        }
        jwt("myJwtAuth") {

            val audience = property("auth.jwt.audience")

            if(testing) {
                val verifier = JWT.require(Algorithm.HMAC256("secret"))
                    .withIssuer(property("auth.jwt.jwkIssuer"))
                    .withAudience(audience)
                    .build(); //Reusable verifier instance

                verifier(verifier)

            }else {
                val jwkIssuer = property("auth.jwt.jwkIssuer")
                val jwkProvider = JwkProviderBuilder(URL(property("auth.jwt.jwkUrl")))
                    .cached(10, 24, TimeUnit.HOURS)
                    .rateLimited(10, 1, TimeUnit.MINUTES)
                    .build()

                verifier(jwkProvider, jwkIssuer)

            }

            validate { credentials ->
                if (credentials.payload.audience.contains(audience)) JWTPrincipal(credentials.payload) else null
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

        authenticate("myBasicAuth") {
            get("/protected") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
        authenticate("myJwtAuth") {
            withRole("admin") {
                val itemService: ItemService by di.instance()
                itemsRoute(itemService)
            }
        }

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
    }
}

val Application.envKind get() = property("ktor.deployment.environment")
val Application.testing get() = envKind == "test"
fun Application.property(key: String) = environment.config.property(key).getString()

