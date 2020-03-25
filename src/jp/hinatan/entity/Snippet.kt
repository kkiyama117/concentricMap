package jp.hinatan.entity

import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class Snippet(val user: String, val text: String)

val snippets = Collections.synchronizedList(
    mutableListOf(
        Snippet(user = "test", text = "hello"),
        Snippet(user = "test", text = "world")
    )
)
