package jp.hinatan

import io.ktor.server.netty.EngineMain

object Main {
    @JvmStatic
    fun main(args: Array<String>): Unit = EngineMain.main(args)
//    fun main(args: Array<String>) = embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}
