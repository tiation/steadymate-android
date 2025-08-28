package com.steadymate.app.domain.model

/**
 * Domain model representing a habit completion record.
 * Simplified to match requirements - binary completion tracking.
 */
data class HabitTick(
    val habitId: String,
    val date: String, // yyyy-MM-dd format
    val done: Boolean
)
