package com.steadymate.app.domain.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a mood entry in the SteadyMate app.
 * This matches the requirements from the prompt for a simple 30-second mood check-in.
 */
data class MoodEntry(
    val id: Long = 0L,
    val userId: String,
    val moodLevel: Int, // 0..10 scale as per requirements
    val emotionTags: List<String>, // Simple tags like "Happy", "Sad", "Anxious"
    val notes: String = "", // Optional notes
    val timestamp: LocalDateTime
)
