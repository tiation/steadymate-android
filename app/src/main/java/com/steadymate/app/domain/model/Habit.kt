package com.steadymate.app.domain.model

import java.util.UUID

/**
 * Domain model representing a habit in the SteadyMate app.
 * Simplified to match requirements - small, binary habits with optional reminders.
 */
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val schedule: String, // e.g., "MTWTF--" for weekdays
    val reminderTime: String? = null, // "07:30" format
    val enabled: Boolean = true
)
