package com.steadymate.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Represents a thought reframing exercise following CBT principles.
 * Pattern: Situation → Thought → Evidence For/Against → Balanced Thought
 */
@Entity(tableName = "reframe_entries")
data class ReframeEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    
    // The triggering situation or event
    val situation: String,
    
    // The automatic/initial thought
    val automaticThought: String,
    
    // Emotional intensity (1-10) when the thought occurred
    val emotionalIntensity: Int,
    
    // Evidence supporting the automatic thought
    val evidenceFor: String,
    
    // Evidence against the automatic thought
    val evidenceAgainst: String,
    
    // The balanced, more realistic thought
    val balancedThought: String,
    
    // New emotional intensity (1-10) after reframing
    val newEmotionalIntensity: Int,
    
    // Optional tags for categorization
    val tags: List<String> = emptyList()
) {
    fun getDateTime(): LocalDateTime {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    fun getImprovementScore(): Int {
        return (emotionalIntensity - newEmotionalIntensity).coerceAtLeast(0)
    }
}

/**
 * Represents a worry entry with timer and parking functionality
 */
@Entity(tableName = "worry_entries")
data class WorryEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    
    // The worry or anxious thought
    val worry: String,
    
    // How long the user spent actively worrying (in seconds)
    val worryTimeSeconds: Int,
    
    // Whether the worry was "parked" for later consideration
    val isParked: Boolean = false,
    
    // Scheduled time to revisit the worry (if parked)
    val scheduledRevisit: String? = null,
    
    // Actions taken to address the worry
    val actionsTaken: List<String> = emptyList(),
    
    // Whether the worry was resolved or addressed
    val isResolved: Boolean = false,
    
    // Anxiety level before (1-10)
    val anxietyBefore: Int,
    
    // Anxiety level after processing (1-10)
    val anxietyAfter: Int? = null,
    
    val category: WorryCategory = WorryCategory.GENERAL
) {
    fun getDateTime(): LocalDateTime {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    fun getScheduledRevisitDateTime(): LocalDateTime? {
        return scheduledRevisit?.let { 
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
        }
    }
}

enum class WorryCategory {
    WORK,
    RELATIONSHIPS,
    HEALTH,
    FINANCES,
    FAMILY,
    FUTURE,
    GENERAL
}

/**
 * Represents a micro-wins or gratitude entry
 */
@Entity(tableName = "micro_wins")
data class MicroWin(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    
    // The positive event or accomplishment
    val description: String,
    
    // Category of the win
    val category: WinCategory,
    
    // How significant it felt (1-5)
    val significance: Int = 3,
    
    // Optional note about why it mattered
    val reflection: String? = null
) {
    fun getDateTime(): LocalDateTime {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}

enum class WinCategory {
    PERSONAL_CARE,
    WORK_ACHIEVEMENT,
    SOCIAL_CONNECTION,
    HEALTH_FITNESS,
    LEARNING_GROWTH,
    KINDNESS_GIVING,
    CREATIVITY,
    OTHER
}

/**
 * Daily CBT insights and patterns
 */
data class CBTInsights(
    val totalReframes: Int,
    val averageImprovementScore: Float,
    val commonThoughtPatterns: List<String>,
    val totalWorryTime: Int, // in minutes
    val worriesResolved: Int,
    val microWinsCount: Int,
    val streakDays: Int
)

/**
 * Common cognitive distortions for pattern recognition
 */
enum class CognitiveDistortion(val displayName: String, val description: String) {
    ALL_OR_NOTHING("All-or-Nothing", "Seeing things in black and white"),
    OVERGENERALIZATION("Overgeneralization", "Making broad conclusions from single events"),
    MENTAL_FILTER("Mental Filter", "Focusing only on negatives"),
    DISCOUNTING_POSITIVE("Discounting Positive", "Dismissing good things that happen"),
    JUMPING_TO_CONCLUSIONS("Jumping to Conclusions", "Mind reading or fortune telling"),
    MAGNIFICATION("Magnification", "Blowing things out of proportion"),
    EMOTIONAL_REASONING("Emotional Reasoning", "I feel it, therefore it must be true"),
    SHOULD_STATEMENTS("Should Statements", "Criticizing with 'should' or 'must'"),
    LABELING("Labeling", "Calling yourself names"),
    PERSONALIZATION("Personalization", "Taking responsibility for things outside your control")
}
