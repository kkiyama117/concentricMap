package jp.hinatan.entity

import com.typesafe.config.Optional
import java.util.Collections
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    @SerialName("user")
    @Optional
    val username: String? = null,
    val text: String
)

val snippets: MutableList<Snippet> = Collections.synchronizedList(
    mutableListOf(
        Snippet(username = "test", text = "hello"),
        Snippet(username = "test", text = "world")
    )
)
