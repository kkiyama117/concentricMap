package jp.hinatan.common.db

import io.ktor.auth.Authentication
import jp.hinatan.common.db.DatabaseFactory.dbQuery
import jp.hinatan.common.exceptions.AuthorizationException
import jp.hinatan.common.exceptions.PostValueException
import jp.hinatan.entity.PostedUser
import jp.hinatan.entity.User
import jp.hinatan.entity.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update


class UserDAO {

    suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map { toEntity(it) }
    }

    suspend fun getUser(id: Int): User? = dbQuery {
        Users.select {
            (Users.id eq id)
        }.mapNotNull { toEntity(it) }
            .singleOrNull()
    }

    suspend fun updateUser(postedUser: PostedUser): User {
        val id = postedUser.id ?: throw PostValueException("id is null")
        val oldUser = getUser(id) ?: throw PostValueException("user not found")
        return run {
            dbQuery {
                Users.update({ Users.id eq id }) {
                    it[name] = postedUser.name ?: oldUser.name
                    it[password] = postedUser.password ?: oldUser.password
                    it[createdAt] = oldUser.createdAt
                    it[updatedAt] = System.currentTimeMillis()
                }
            }
            getUser(id)!!
        }
    }

    suspend fun addUser(postedUser: PostedUser): User {
        var key = 0
        if (postedUser.name == null || postedUser.password == null) {
            throw Exception("value error")
        }
        return run {
            dbQuery {
                key = (Users.insert {
                    it[name] = postedUser.name
                    it[password] = postedUser.password
                    it[createdAt] = System.currentTimeMillis()
                    it[updatedAt] = System.currentTimeMillis()
                } get Users.id)
            }
            getUser(key)!!
        }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return dbQuery {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }

    // change DB row to Entity class
    private fun toEntity(row: ResultRow): User =
        User(
            id = row[Users.id],
            name = row[Users.name],
            password = row[Users.password],
            createdAt = row[Users.createdAt],
            updatedAt = row[Users.updatedAt]
        )
}