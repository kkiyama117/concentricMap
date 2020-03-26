package jp.hinatan.routes

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.websocket.webSocket
import jp.hinatan.auth.simpleJwt
import jp.hinatan.entity.LoginRegister
import jp.hinatan.entity.Snippet
import jp.hinatan.entity.User
import jp.hinatan.entity.snippets
import jp.hinatan.entity.users
import jp.hinatan.exceptions.AuthenticationException
import jp.hinatan.exceptions.AuthorizationException

/**
 * Route setting for server
 */
@KtorExperimentalLocationsAPI
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
        if (user.password != post.password) error("Invalid credentials")
        call.respond(mapOf("token" to simpleJwt.sign(user.name)))
    }

    get<MyLocation> {
        call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
    }
    // Register nested routes
    get<Type.Edit> {
        call.respondText("Inside $it")
    }
    get<Type.List> {
        call.respondText("Inside $it")
    }

    get("/health_check") {
        // Check databases/other services.
        call.respond(mapOf("Status" to "OK"))
    }

    webSocket("/myws/echo") {
        send(Frame.Text("Hi from server"))
        while (true) {
            val frame = incoming.receive()
            if (frame is Frame.Text) {
                send(Frame.Text("Client said: " + frame.readText()))
            }
        }
    }
}

fun webSocket() {
}
