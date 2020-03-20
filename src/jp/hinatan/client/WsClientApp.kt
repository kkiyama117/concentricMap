package jp.hinatan.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.channels.filterNotNull
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.runBlocking

object WsClientApp {
    @KtorExperimentalAPI
    @JvmStatic
    fun main(args: Array<String>) {
        /**
         * Config for Client
         */
        val client = HttpClient(CIO) {
            install(Auth) {
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            install(Logging) {
                level = LogLevel.HEADERS
            }
            install(WebSockets)
            BrowserUserAgent() // install default browser-like user-agent
            // install(UserAgent) { agent = "some user agent" }
        }
        runBlocking {
            client.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/myws/echo") {
                send(Frame.Text("Hello World"))
                for (message in incoming.map { it as? Frame.Text }.filterNotNull()) {
                    println("Server said: " + message.readText())
                }
            }
            // Sample for making a HTTP Client request
            /*
            val message = client.post<JsonSampleClass> {
                url("http://127.0.0.1:8080/path/to/endpoint")
                contentType(ContentType.Application.Json)
                body = JsonSampleClass(hello = "world")
            }
            */
        }
    }
}
