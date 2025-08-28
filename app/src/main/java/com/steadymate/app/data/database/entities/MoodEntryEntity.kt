package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.steadymate.app.data.database.converters.LocalDateTimeConverter
import com.steadymate.app.data.database.converters.StringListConverter
import com.steadymate.app.domain.model.MoodEntry
import kotlinx.datetime.LocalDateTime

/**
 * Room entity representing a mood entry in the database.
 * Simplified to match the requirements for quick 30-second check-ins.
 */
@Entity(
    tableName = "mood_entries",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["userId"]),
        Index(value = ["moodLevel"])
    ]
)
@TypeConverters(LocalDateTimeConverter::class, StringListConverter::class)
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: String,
    val moodLevel: Int, // 0..10 scale
    val emotionTags: List<String>, // Simple tags like "Happy", "Sad", "Anxious"
    val notes: String = "", // Optional notes
    val timestamp: LocalDateTime
)

/**
 * Extension function to convert MoodEntryEntity to domain MoodEntry model
 */
fun MoodEntryEntity.toDomainModel(): MoodEntry {
    return MoodEntry(
        id = id,
        userId = userId,
        moodLevel = moodLevel,
        emotionTags = emotionTags,
        notes = notes,
        timestamp = timestamp
    )
}

/**
 * Extension function to convert domain MoodEntry model to MoodEntryEntity
 */
fun MoodEntry.toEntity(): MoodEntryEntity {
    return MoodEntryEntity(
        id = id,
        userId = userId,
        moodLevel = moodLevel,
        emotionTags = emotionTags,
        notes = notes,
        timestamp = timestamp
    )
}
