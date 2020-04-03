package jp.hinatan.entity

import java.util.Collections
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val id: Int,
    val name: String,
    val password: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class PostedUser(
    val id: Int?,
    val name: String?,
    val password: String?,
    val createdAt: Long?,
    val updatedAt: Long?
)


// for DB
object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val password = varchar("password", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    override val primaryKey = PrimaryKey(id)
}
