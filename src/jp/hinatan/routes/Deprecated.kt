package jp.hinatan.routes

import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.respondText
import io.ktor.routing.Routing
import jp.hinatan.routes.entity.MyLocation
import jp.hinatan.routes.entity.Type

@KtorExperimentalLocationsAPI
fun Routing.deprecated(){
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
}