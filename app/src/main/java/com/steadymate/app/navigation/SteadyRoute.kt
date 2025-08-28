package com.steadymate.app.navigation

import kotlinx.serialization.Serializable

/**
 * Sealed class representing all possible routes in the SteadyMate app.
 * Uses kotlinx.serialization for type-safe navigation arguments.
 */
@Serializable
sealed class SteadyRoute {
    
    // Bottom navigation destinations
    @Serializable
    data object Home : SteadyRoute()
    
    @Serializable
    data object Tools : SteadyRoute()
    
    @Serializable
    data object Habits : SteadyRoute()
    
    @Serializable
    data object Insights : SteadyRoute()
    
    @Serializable
    data object Settings : SteadyRoute()
    
    // Crisis overlay route (not part of bottom navigation)
    @Serializable
    data object Crisis : SteadyRoute()
    
    // Daily Check-In route
    @Serializable
    data object CheckIn : SteadyRoute()
    
    // Onboarding routes
    @Serializable
    data class Onboarding(val step: Int) : SteadyRoute()
    
    // Tool-specific routes
    @Serializable
    data object Breathe : SteadyRoute()
    
    @Serializable
    data object Reframe : SteadyRoute()
    
    @Serializable
    data object WorryTimer : SteadyRoute()
    
    @Serializable
    data object ThreeGoodThings : SteadyRoute()
    
    // CBT Tool routes
    @Serializable
    data object CBTReframe : SteadyRoute()
    
    @Serializable
    data object CBTWorryTimer : SteadyRoute()
    
    @Serializable
    data object CBTMicroWins : SteadyRoute()
    
    // Habit management routes
    @Serializable
    data class HabitEdit(val habitId: String? = null) : SteadyRoute()
    
    // Crisis plan management
    @Serializable
    data object CrisisPlan : SteadyRoute()
    
    // Nested routes with arguments
    @Serializable
    data class HabitDetail(val habitId: String) : SteadyRoute()
    
    @Serializable
    data class ToolDetail(val toolId: String) : SteadyRoute()
    
    @Serializable
    data class InsightDetail(val insightId: String) : SteadyRoute()
    
    @Serializable
    data class SettingsCategory(val category: String) : SteadyRoute()
}

/**
 * Helper object to get route strings and handle navigation
 */
object SteadyRoutes {
    val bottomNavRoutes = listOf(
        SteadyRoute.Home,
        SteadyRoute.Tools,
        SteadyRoute.Habits,
        SteadyRoute.Insights,
        SteadyRoute.Settings
    )
    
    fun isBottomNavRoute(route: SteadyRoute): Boolean {
        return bottomNavRoutes.contains(route)
    }
}
