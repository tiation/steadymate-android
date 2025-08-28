package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.steadymate.app.domain.model.BreathingSession
import kotlinx.datetime.LocalDateTime

/**
 * Room entity representing a breathing session in the database.
 * This tracks completed breathing exercises and their effectiveness.
 */
@Entity(
    tableName = "breathing_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["timestamp"]),
        Index(value = ["exerciseType"])
    ]
)
data class BreathingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val exerciseType: String, // "box", "4-7-8", "custom"
    val duration: Int, // Duration in seconds
    val completedRounds: Int,
    val targetRounds: Int,
    val stressBefore: Int? = null, // 1-10 scale
    val stressAfter: Int? = null, // 1-10 scale
    val moodBefore: Int? = null, // 1-10 scale
    val moodAfter: Int? = null, // 1-10 scale
    val timestamp: LocalDateTime,
    val notes: String = ""
)

/**
 * Extension function to convert BreathingSessionEntity to domain BreathingSession model
 */
fun BreathingSessionEntity.toDomainModel(): BreathingSession {
    return BreathingSession(
        id = id,
        userId = userId,
        exerciseType = exerciseType,
        duration = duration,
        completedRounds = completedRounds,
        targetRounds = targetRounds,
        stressBefore = stressBefore,
        stressAfter = stressAfter,
        moodBefore = moodBefore,
        moodAfter = moodAfter,
        timestamp = timestamp,
        notes = notes
    )
}

/**
 * Extension function to convert domain BreathingSession model to BreathingSessionEntity
 */
fun BreathingSession.toEntity(): BreathingSessionEntity {
    return BreathingSessionEntity(
        id = id,
        userId = userId,
        exerciseType = exerciseType,
        duration = duration,
        completedRounds = completedRounds,
        targetRounds = targetRounds,
        stressBefore = stressBefore,
        stressAfter = stressAfter,
        moodBefore = moodBefore,
        moodAfter = moodAfter,
        timestamp = timestamp,
        notes = notes
    )
}
