package jp.hinatan.exceptions

/**
 * Exception threw in JWT Authentication
 */
class InvalidCredentialsException(message: String) : AuthenticationException(message)
