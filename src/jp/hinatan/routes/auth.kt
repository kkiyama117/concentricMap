package jp.hinatan.routes

import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import jp.hinatan.common.auth.JwtConfig
import jp.hinatan.common.auth.Token
import jp.hinatan.entity.PostedUser
import jp.hinatan.usecases.UserService

fun Routing.users() {
    route("/users") {
        // TODO: Use Firebase
        post {
            val newUser = call.receive<PostedUser>()
            call.respond(UserService.addUser(newUser))
        }
    }
    authenticate {

    }
    route("/login") {
        post {
            // get client ip behind proxy
            // https://github.com/ktorio/ktor/issues/351
            UserService.findUserByCredentials(call.receive<UserPasswordCredential>()).let { u ->
                u?.name?.let { it1 -> JwtConfig.sign(it1) }?.let { it2 -> Token(it2) }?.let { it3 -> call.respond(it3) }
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
