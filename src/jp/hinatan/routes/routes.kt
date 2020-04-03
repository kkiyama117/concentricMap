package jp.hinatan.routes

import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import jp.hinatan.entity.Snippet
import jp.hinatan.entity.snippets
import jp.hinatan.usecases.UserService.getUserByPrincipal

/**
 * Route setting for server
 */
fun Routing.routes() {
    // json test
    route("/snippets") {
        get {
//        call.respond(mapOf("OK" to true))
            call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
        }
        authenticate {
            post {
                val post = call.receive<Snippet>()
                // use authorization feature
                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                snippets += Snippet(principal.name, post.text)
                call.respond(mapOf("OK" to true))
//                call.authentication.principal<JWTPrincipal>()
//                    ?.let { princ->call.respond(authGetUsers(princ)) }
//                    ?.call.respond(HttpStatusCode.Unauthorized)
            }
        }
        authenticate {
            get("/secret") {
                call.authentication.principal<JWTPrincipal>()
                    ?.let { princ -> getUserByPrincipal(princ)?.let { it1 -> call.respond(it1) } }
                    ?: call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
    get("/") {
        // Check databases/other services.
        call.respond(mapOf("Status" to "OK", "version" to "1.2.2"))
    }

    get("/health_check") {
        // Check databases/other services.
        call.respond(mapOf("Status" to "OK"))
    }
}
