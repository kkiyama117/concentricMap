package jp.hinatan.common.exceptions

/**
 * Exception threw in JWT Authentication
 */
open class InvalidCredentialsException(message: String) : AuthenticationException(message)
