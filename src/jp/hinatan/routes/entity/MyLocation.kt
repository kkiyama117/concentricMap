package jp.hinatan.routes.entity

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")
