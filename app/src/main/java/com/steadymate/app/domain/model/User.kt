package com.steadymate.app.domain.model

import kotlinx.datetime.LocalDate

/**
 * Domain model representing a user in the SteadyMate app.
 * This is a clean domain entity without any framework dependencies.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val joinDate: LocalDate,
    val streakCount: Int = 0,
    val isActive: Boolean = true
)
