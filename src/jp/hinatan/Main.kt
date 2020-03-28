package jp.hinatan

import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty

//fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)
fun main(args: Array<String>) = embeddedServer(Jetty, commandLineEnvironment(args)).start(wait = true)

