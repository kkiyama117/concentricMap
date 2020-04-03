package jp.hinatan.usecases


import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.client.features.auth.Auth
import jp.hinatan.common.db.UserDAO
import jp.hinatan.entity.AuthCredential
import jp.hinatan.entity.PostedUser
import jp.hinatan.entity.User
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

val userDAO = UserDAO()


object UserService {

    fun getUserByPrincipal(token: JWTPrincipal): User? = runBlocking {
        userDAO.getUser(token.payload.getClaim("id").asInt())
    }

    fun findUserByCredentials(credential: AuthCredential): User? = runBlocking {
        userDAO.getUsers().firstOrNull { it.password == credential.password && it.name == credential.name }
    }

    fun addUser(postedUser: PostedUser): User = runBlocking {
        userDAO.addUser(
            postedUser
        )
    }

    fun updateUser(postedUser: PostedUser): User = runBlocking {
        userDAO.updateUser(
            postedUser
        )
    }
}
