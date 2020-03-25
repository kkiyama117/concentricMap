package jp.hinatan.routes

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
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
import io.ktor.websocket.webSocket
import jp.hinatan.entity.Snippet
import jp.hinatan.exceptions.AuthenticationException
import jp.hinatan.exceptions.AuthorizationException
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul
import java.util.*

/**
 * Route setting for server
 */
@KtorExperimentalLocationsAPI
fun Routing.routes() {
    get("/") {
        call.respondText("HELLO WORLD2!", contentType = ContentType.Text.Plain)
    }

    // json test
    val snippets = Collections.synchronizedList(
        mutableListOf(
            Snippet("hello"),
            Snippet("world")
        )
    )
    get("/snippets") {
//        call.respond(mapOf("OK" to true))
        call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
    }
    post("/snippets") {
        val post = call.receive<Snippet>()
        snippets += Snippet(post.text)
        call.respond(mapOf("OK" to true))
    }

    get("/html-dsl") {
        call.respondHtml {
            body {
                h1 { +"HTML" }
                ul {
                    for (n in 1..10) {
                        li { +"$n" }
                    }
                }
            }
        }
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

    // status page
    // Also see routing
    install(StatusPages) {
        exception<AuthenticationException> {
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> {
            call.respond(HttpStatusCode.Forbidden)
        }
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
