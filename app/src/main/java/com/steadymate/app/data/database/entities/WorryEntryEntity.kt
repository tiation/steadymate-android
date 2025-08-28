package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.steadymate.app.domain.model.WorryEntry
import com.steadymate.app.domain.model.WorryCategory
import com.steadymate.app.data.database.converters.StringListConverter

/**
 * Room entity representing a worry entry in the database.
 * Matches the domain WorryEntry model structure.
 */
@Entity(
    tableName = "worry_entries",
    indices = [
        Index(value = ["timestamp"])
    ]
)
@TypeConverters(StringListConverter::class)
data class WorryEntryEntity(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val worry: String,
    val worryTimeSeconds: Int,
    val isParked: Boolean = false,
    val scheduledRevisit: String? = null,
    val actionsTaken: List<String> = emptyList(),
    val isResolved: Boolean = false,
    val anxietyBefore: Int,
    val anxietyAfter: Int? = null,
    val category: WorryCategory = WorryCategory.GENERAL
)

/**
 * Extension function to convert WorryEntryEntity to domain WorryEntry model
 */
fun WorryEntryEntity.toDomainModel(): WorryEntry {
    return WorryEntry(
        id = id,
        timestamp = timestamp,
        worry = worry,
        worryTimeSeconds = worryTimeSeconds,
        isParked = isParked,
        scheduledRevisit = scheduledRevisit,
        actionsTaken = actionsTaken,
        isResolved = isResolved,
        anxietyBefore = anxietyBefore,
        anxietyAfter = anxietyAfter,
        category = category
    )
}

/**
 * Extension function to convert domain WorryEntry model to WorryEntryEntity
 */
fun WorryEntry.toEntity(): WorryEntryEntity {
    return WorryEntryEntity(
        id = id,
        timestamp = timestamp,
        worry = worry,
        worryTimeSeconds = worryTimeSeconds,
        isParked = isParked,
        scheduledRevisit = scheduledRevisit,
        actionsTaken = actionsTaken,
        isResolved = isResolved,
        anxietyBefore = anxietyBefore,
        anxietyAfter = anxietyAfter,
        category = category
    )
}
