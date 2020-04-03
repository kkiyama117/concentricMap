package jp.hinatan.common.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.auth.jwt.JWTAuthenticationProvider
import jp.hinatan.entity.User
import java.util.*

object JwtConfig {
    const val issuer = "hinatan.jp"
    private const val secret = "my-super-secret-for-jwt"
    private const val validityInMs = 3_600_000 * 10 // 10 hours
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun sign(name: String): String = JWT.create().withClaim("name", name)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}
