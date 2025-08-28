package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.steadymate.app.domain.model.MicroWin
import com.steadymate.app.domain.model.WinCategory

/**
 * Room entity representing a micro win entry in the database.
 * Matches the domain MicroWin model structure.
 */
@Entity(
    tableName = "micro_wins",
    indices = [
        Index(value = ["timestamp"])
    ]
)
data class MicroWinEntity(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val description: String,
    val category: WinCategory,
    val significance: Int = 3,
    val reflection: String? = null
)

/**
 * Extension function to convert MicroWinEntity to domain MicroWin model
 */
fun MicroWinEntity.toDomainModel(): MicroWin {
    return MicroWin(
        id = id,
        timestamp = timestamp,
        description = description,
        category = category,
        significance = significance,
        reflection = reflection
    )
}

/**
 * Extension function to convert domain MicroWin model to MicroWinEntity
 */
fun MicroWin.toEntity(): MicroWinEntity {
    return MicroWinEntity(
        id = id,
        timestamp = timestamp,
        description = description,
        category = category,
        significance = significance,
        reflection = reflection
    )
}
