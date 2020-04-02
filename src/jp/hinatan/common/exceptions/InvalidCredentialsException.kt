package jp.hinatan.common.exceptions

/**
 * Exception threw in JWT Authentication
 */
class InvalidCredentialsException(message: String) : AuthenticationException(message)
