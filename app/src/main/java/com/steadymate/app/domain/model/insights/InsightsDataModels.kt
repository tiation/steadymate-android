package com.steadymate.app.domain.model.insights

import kotlinx.datetime.LocalDateTime

/**
 * Data transfer objects for insights aggregation queries.
 * These models represent the output of Room queries for analytics.
 */

/**
 * Daily mood aggregate data from database queries
 */
data class DailyMoodAggregate(
    val date: LocalDateTime,
    val averageMood: Double,
    val entryCount: Int,
    val minMood: Int,
    val maxMood: Int,
    val dominantEmotion: String? = null
)

/**
 * Emotion frequency data
 */
data class EmotionCount(
    val emotion: String,
    val frequency: Int,
    val totalEntries: Int,
    val averageMood: Double
)

/**
 * Activity impact data
 */
data class ActivityImpact(
    val activity: String,
    val occurrences: Int,
    val averageMood: Double,
    val moodSum: Double,
    val entryCount: Int
)

/**
 * Streak calculation data
 */
data class StreakData(
    val date: LocalDateTime,
    val hasEntry: Boolean
)

/**
 * Weekly aggregation data
 */
data class WeeklyAggregate(
    val weekStart: LocalDateTime,
    val weekEnd: LocalDateTime,
    val entryCount: Int,
    val averageMood: Double,
    val minMood: Int,
    val maxMood: Int,
    val topEmotions: List<String>
)

/**
 * Mood trend calculation data
 */
data class MoodTrendCalculation(
    val earlierPeriodAverage: Double,
    val laterPeriodAverage: Double,
    val volatility: Double,
    val consistencyScore: Double
)

/**
 * Time period constants for database queries
 */
object TimePeriods {
    const val MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000L
    const val MILLISECONDS_IN_WEEK = 7 * MILLISECONDS_IN_DAY
    const val MILLISECONDS_IN_MONTH = 30 * MILLISECONDS_IN_DAY
}
