package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.steadymate.app.domain.model.ReframeEntry
import com.steadymate.app.data.database.converters.StringListConverter
import kotlinx.datetime.LocalDateTime

/**
 * Room entity representing a cognitive reframing entry in the database.
 * Matches the domain ReframeEntry model structure.
 */
@Entity(
    tableName = "reframe_entries",
    indices = [
        Index(value = ["timestamp"])
    ]
)
@TypeConverters(StringListConverter::class)
data class ReframeEntryEntity(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val situation: String,
    val automaticThought: String,
    val emotionalIntensity: Int,
    val evidenceFor: String,
    val evidenceAgainst: String,
    val balancedThought: String,
    val newEmotionalIntensity: Int,
    val tags: List<String> = emptyList()
)

/**
 * Extension function to convert ReframeEntryEntity to domain ReframeEntry model
 */
fun ReframeEntryEntity.toDomainModel(): ReframeEntry {
    return ReframeEntry(
        id = id,
        timestamp = timestamp,
        situation = situation,
        automaticThought = automaticThought,
        emotionalIntensity = emotionalIntensity,
        evidenceFor = evidenceFor,
        evidenceAgainst = evidenceAgainst,
        balancedThought = balancedThought,
        newEmotionalIntensity = newEmotionalIntensity,
        tags = tags
    )
}

/**
 * Extension function to convert domain ReframeEntry model to ReframeEntryEntity
 */
fun ReframeEntry.toEntity(): ReframeEntryEntity {
    return ReframeEntryEntity(
        id = id,
        timestamp = timestamp,
        situation = situation,
        automaticThought = automaticThought,
        emotionalIntensity = emotionalIntensity,
        evidenceFor = evidenceFor,
        evidenceAgainst = evidenceAgainst,
        balancedThought = balancedThought,
        newEmotionalIntensity = newEmotionalIntensity,
        tags = tags
    )
}
