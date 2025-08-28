package com.steadymate.app.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing a breathing session.
 * This tracks completed breathing exercises and their effectiveness.
 */
data class BreathingSession(
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
