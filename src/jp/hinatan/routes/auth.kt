package jp.hinatan.routes

import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import jp.hinatan.common.auth.JwtConfig
import jp.hinatan.common.exceptions.PostValueException
import jp.hinatan.entity.PostedUser
import jp.hinatan.usecases.UserService

fun Routing.users() {
    route("/users") {
        post {
            val newUser = call.receive<PostedUser>()
            call.respond(UserService.addUser(newUser))
        }
    }
    authenticate {
        route("/user") {
            get {
                val principal = call.principal<JWTPrincipal>() ?: error("No Principal")
                val user = UserService.getUserByPrincipal(principal) ?: error("No User")
                call.respond(user)
            }
        }
    }

    route("/login") {
        post {
            // get client ip behind proxy
            // https://github.com/ktorio/ktor/issues/351
            UserService.findUserByCredentials(call.receive()).let { user ->
                user?.name?.let { it -> JwtConfig.sign(it) }?.let { token ->
                    call.respond(mapOf("token" to token))
                }
            } ?: throw PostValueException("User not found")
        }
    }
}
