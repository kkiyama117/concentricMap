package jp.hinatan.entity

import io.ktor.auth.Credential
import kotlinx.serialization.Serializable

@Serializable
data class AuthCredential(val name: String, val password: String) : Credential
