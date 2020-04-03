package jp.hinatan

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.content.TextContent
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.minimumSize
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.http.withCharset
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.json
import io.ktor.util.KtorExperimentalAPI
import jp.hinatan.common.auth.JwtConfig
import jp.hinatan.common.db.DatabaseFactory
import jp.hinatan.common.db.UserDAO
import jp.hinatan.common.exceptions.AuthenticationException
import jp.hinatan.common.exceptions.AuthorizationException
import jp.hinatan.common.exceptions.PostValueException
import jp.hinatan.routes.deprecated
import jp.hinatan.routes.routes
import jp.hinatan.routes.users
import jp.hinatan.routes.webSocket
import org.slf4j.event.Level
import java.time.Duration

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    // TODO: UserDAO
    DatabaseFactory.init()

    install(Locations) {
        // set URL
    }

    /**
     * Compress resources
     */
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(AutoHeadResponse)

    // Logging
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    // CORS header setting
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost()
        // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
    // https://ktor.io/servers/features/https-redirect.html#testing
//    if (!testing) {
//        install(HttpsRedirect) {
    // The port to redirect to. By default 443, the default HTTPS port.
//            sslPort = 443
    // 301 Moved Permanently, or 302 Found redirect.
//            permanentRedirect = true
//        }
//    }

    /**
     * Use WebSocket
     * https://ktor.io/servers/features/websockets.html
     */
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // Authentication
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
//            realm = JwtConfig.issuer
            validate {
                with(it.payload) {
                    if (getClaim("name").isNull) {
                        null
                    } else {
                        JWTPrincipal(it.payload)
                    }
                }
            }
        }
    }

    // JSON
    install(ContentNegotiation) {
        json(
            DefaultJsonConfiguration.copy(
                prettyPrint = true
            )
        )
    }

    // status page
    // Also see routing
    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(
                TextContent(
                    "${it.value} ${it.description}",
                    ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                    it
                )
            )
//            call.respond(
//                HttpStatusCode.NotFound,
//                mapOf("status" to ContentType.Text)
//            )
        }
        // About Auth
        exception<AuthenticationException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "NG", "error" to (exception.message ?: "")))
        }
        exception<AuthorizationException> {
            call.respond(HttpStatusCode.Forbidden)
        }
        // Any other exception
        exception<PostValueException> { exception ->
            call.respond(HttpStatusCode.BadRequest, mapOf("status" to "NG", "error" to (exception.message ?: "")))
        }
//        exception<Throwable> {
//            call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "NG", "error" to (this.toString())))
//        }
    }

    routing {
        routes()
        users()
        webSocket()
        deprecated()
    }
}
