package com.steadymate.app.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing a safety plan for crisis management.
 * This contains strategies, warning signs, and resources to help during difficult times.
 */
data class SafetyPlan(
    val id: Long = 0,
    val userId: String,
    val warningSigns: List<String>, // Early warning signs of crisis
    val copingStrategies: List<String>, // Internal coping strategies
    val reasonsToLive: List<String>, // Reasons for living/what matters to user
    val distractionActivities: List<String>, // Healthy distractions
    val lastUpdated: LocalDateTime,
    val isActive: Boolean = true
)
