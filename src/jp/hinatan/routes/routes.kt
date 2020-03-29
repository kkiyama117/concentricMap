package jp.hinatan.routes

import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import jp.hinatan.auth.simpleJwt
import jp.hinatan.entity.LoginRegister
import jp.hinatan.entity.Snippet
import jp.hinatan.entity.User
import jp.hinatan.entity.snippets
import jp.hinatan.entity.users
import jp.hinatan.exceptions.InvalidCredentialsException

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
            }
        }
    }
    // auth test
    // TODO: Use Firebase
    post("/login-register") {
        val post = call.receive<LoginRegister>()
        val user = users.getOrPut(post.name) { User(post.name, post.password) }
        if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
        call.respond(mapOf("token" to simpleJwt.sign(user.name)))
    }

    get("/") {
        // Check databases/other services.
        call.respond(mapOf("Status" to "OK"))
    }

    get("/health_check") {
        // Check databases/other services.
        call.respond(mapOf("Status" to "OK"))
    }
}
