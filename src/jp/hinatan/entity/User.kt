package jp.hinatan.entity

import java.util.Collections
import kotlinx.serialization.Serializable

@Serializable
class User(val name: String, val password: String)

val users: MutableMap<String, User> = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)

@Serializable
class LoginRegister(val name: String, val password: String)
