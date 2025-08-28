package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.steadymate.app.domain.model.User
import kotlinx.datetime.LocalDate

/**
 * Room entity representing a user in the database.
 * This is the data layer representation that corresponds to the User domain model.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val joinDate: LocalDate,
    val streakCount: Int = 0,
    val isActive: Boolean = true
)

/**
 * Extension function to convert UserEntity to domain User model
 */
fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        joinDate = joinDate,
        streakCount = streakCount,
        isActive = isActive
    )
}

/**
 * Extension function to convert domain User model to UserEntity
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        joinDate = joinDate,
        streakCount = streakCount,
        isActive = isActive
    )
}
