package com.steadymate.app.domain.model

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

/**
 * Represents a habit that the user wants to track
 */
@Serializable
data class HabitData(
    val id: String,
    val name: String,
    val description: String = "",
    val icon: String = "⭐",
    val category: HabitCategory = HabitCategory.WELLBEING,
    val targetFrequency: HabitFrequency = HabitFrequency.DAILY,
    val reminderTime: String? = null, // Format: HH:mm
    val reminderDays: List<Int> = listOf(1,2,3,4,5,6,7), // 1=Mon, 7=Sun
    val isActive: Boolean = true,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val color: String = "#6200EA" // Hex color for the habit
) {
    val formattedReminderTime: String?
        get() = reminderTime?.let { time ->
            val parts = time.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: return null
            
            val amPm = if (hour < 12) "AM" else "PM" 
            val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            
            String.format("%d:%02d %s", displayHour, minute, amPm)
        }
    
    val reminderDaysText: String
        get() = when {
            reminderDays.size == 7 -> "Every day"
            reminderDays.size == 5 && reminderDays.containsAll(listOf(1,2,3,4,5)) -> "Weekdays"
            reminderDays.size == 2 && reminderDays.containsAll(listOf(6,7)) -> "Weekends"
            else -> {
                val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                reminderDays.sorted().joinToString(", ") { dayNames[it - 1] }
            }
        }
}

/**
 * Represents a completion record for a habit on a specific date
 */
@Serializable
data class HabitCompletion(
    val id: String,
    val habitId: String,
    val date: String, // Format: yyyy-MM-dd
    val isCompleted: Boolean,
    val completedAt: Long? = null,
    val notes: String = "",
    val skipReason: String? = null
) {
    val localDate: LocalDate
        get() = LocalDate.parse(date)
        
    val wasSkipped: Boolean
        get() = !isCompleted && skipReason != null
}

/**
 * Statistics for a habit over a time period
 */
data class HabitStats(
    val habitId: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionRate: Float = 0f, // 0.0 to 1.0
    val totalCompletions: Int = 0,
    val totalDays: Int = 0,
    val completionsThisWeek: Int = 0,
    val completionsThisMonth: Int = 0,
    val lastCompleted: LocalDate? = null
) {
    val completionPercentage: Int
        get() = (completionRate * 100).toInt()
        
    val isOnStreak: Boolean
        get() = currentStreak > 0 && lastCompleted == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

/**
 * Categories for organizing habits
 */
enum class HabitCategory(
    val displayName: String,
    val description: String,
    val defaultIcon: String,
    val color: String
) {
    WELLBEING(
        "Wellbeing", 
        "Mental and emotional health habits",
        "🧘",
        "#4CAF50"
    ),
    PHYSICAL(
        "Physical", 
        "Exercise, movement, and physical health",
        "💪",
        "#FF9800"
    ),
    MINDFULNESS(
        "Mindfulness", 
        "Meditation, reflection, and present-moment awareness",
        "🌸",
        "#9C27B0"
    ),
    SOCIAL(
        "Social", 
        "Connection and relationship building",
        "👥",
        "#2196F3"
    ),
    PRODUCTIVITY(
        "Productivity", 
        "Work, learning, and personal development",
        "📚",
        "#FF5722"
    ),
    SELFCARE(
        "Self Care", 
        "Personal care and comfort activities",
        "🛁",
        "#E91E63"
    ),
    NUTRITION(
        "Nutrition", 
        "Eating habits and dietary choices",
        "🥗",
        "#8BC34A"
    ),
    SLEEP(
        "Sleep", 
        "Rest and sleep quality habits",
        "😴",
        "#673AB7"
    )
}

/**
 * Frequency patterns for habits
 */
enum class HabitFrequency(
    val displayName: String,
    val description: String
) {
    DAILY("Daily", "Every day"),
    WEEKLY("Weekly", "Once per week"),
    WEEKDAYS("Weekdays", "Monday to Friday"),
    WEEKENDS("Weekends", "Saturday and Sunday"),
    CUSTOM("Custom", "Custom schedule")
}

/**
 * Pre-defined habit templates for men's mental health
 */
object HabitTemplates {
    val MENTAL_HEALTH_HABITS = listOf(
        HabitTemplate(
            name = "Morning Check-in",
            description = "Rate your mood and set intentions for the day",
            category = HabitCategory.WELLBEING,
            icon = "🌅",
            suggestedTime = "08:00"
        ),
        HabitTemplate(
            name = "5-Minute Breathing",
            description = "Practice deep breathing exercises",
            category = HabitCategory.MINDFULNESS,
            icon = "🧘",
            suggestedTime = "07:30"
        ),
        HabitTemplate(
            name = "Daily Walk",
            description = "Take a 10-15 minute walk outside",
            category = HabitCategory.PHYSICAL,
            icon = "🚶",
            suggestedTime = "17:00"
        ),
        HabitTemplate(
            name = "Gratitude Note",
            description = "Write down one thing you're grateful for",
            category = HabitCategory.WELLBEING,
            icon = "🙏",
            suggestedTime = "21:00"
        ),
        HabitTemplate(
            name = "Connect with Someone",
            description = "Reach out to a friend or family member",
            category = HabitCategory.SOCIAL,
            icon = "📱",
            suggestedTime = "19:00"
        ),
        HabitTemplate(
            name = "Drink Water",
            description = "Have a glass of water when you wake up",
            category = HabitCategory.SELFCARE,
            icon = "💧",
            suggestedTime = "07:00"
        ),
        HabitTemplate(
            name = "Screen-Free Time",
            description = "30 minutes without phones or screens",
            category = HabitCategory.WELLBEING,
            icon = "📵",
            suggestedTime = "20:00"
        ),
        HabitTemplate(
            name = "Journal Entry",
            description = "Write 3 sentences about your day",
            category = HabitCategory.WELLBEING,
            icon = "📝",
            suggestedTime = "21:30"
        ),
        HabitTemplate(
            name = "Stretch or Yoga",
            description = "5-10 minutes of gentle stretching",
            category = HabitCategory.PHYSICAL,
            icon = "🤸",
            suggestedTime = "08:30"
        ),
        HabitTemplate(
            name = "Read for 15 Minutes",
            description = "Read something meaningful or enjoyable",
            category = HabitCategory.PRODUCTIVITY,
            icon = "📖",
            suggestedTime = "20:30"
        ),
        HabitTemplate(
            name = "Healthy Breakfast",
            description = "Eat a nutritious breakfast to start your day",
            category = HabitCategory.NUTRITION,
            icon = "🥣",
            suggestedTime = "08:00"
        ),
        HabitTemplate(
            name = "Evening Wind-down",
            description = "Prepare for sleep with a calming routine",
            category = HabitCategory.SLEEP,
            icon = "🌙",
            suggestedTime = "21:00"
        )
    )
}

/**
 * Template for creating new habits
 */
data class HabitTemplate(
    val name: String,
    val description: String,
    val category: HabitCategory,
    val icon: String,
    val suggestedTime: String? = null,
    val frequency: HabitFrequency = HabitFrequency.DAILY
) {
    fun toHabitData(): HabitData {
        return HabitData(
            id = generateId(),
            name = name,
            description = description,
            icon = icon,
            category = category,
            targetFrequency = frequency,
            reminderTime = suggestedTime,
            color = category.color
        )
    }
    
    private fun generateId(): String {
        return java.util.UUID.randomUUID().toString()
    }
}

/**
 * Skip reasons for when users don't complete habits
 */
enum class SkipReason(val displayName: String, val emoji: String) {
    TOO_BUSY("Too busy", "⏰"),
    NOT_FEELING_WELL("Not feeling well", "🤒"),
    FORGOT("Forgot", "🤦"),
    NOT_MOTIVATED("Not motivated", "😑"),
    CIRCUMSTANCES("Circumstances", "🤷"),
    PLANNED_REST("Planned rest day", "😌"),
    OTHER("Other", "💭")
}

/**
 * UI state for habit completion
 */
data class HabitCompletionState(
    val habit: HabitData,
    val completion: HabitCompletion?,
    val stats: HabitStats,
    val canComplete: Boolean = true,
    val showSkipDialog: Boolean = false
) {
    val isCompleted: Boolean = completion?.isCompleted == true
    val isSkipped: Boolean = completion?.wasSkipped == true
    val canSkip: Boolean = !isCompleted && canComplete
}
