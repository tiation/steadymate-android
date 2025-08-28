package com.steadymate.app.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing an insight generated from mood data
 */
data class Insight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val value: String,
    val trend: TrendDirection,
    val confidence: Float, // 0.0 to 1.0
    val generatedAt: LocalDateTime,
    val actionable: Boolean = false,
    val recommendation: String? = null
)

/**
 * Types of insights that can be generated
 */
enum class InsightType {
    MOOD_TREND,
    EMOTION_PATTERN, 
    ACTIVITY_CORRELATION,
    STREAK_ANALYSIS,
    IMPROVEMENT_OPPORTUNITY,
    POSITIVE_PATTERN,
    WARNING_SIGN
}

/**
 * Direction of trend for insights
 */
enum class TrendDirection {
    IMPROVING,
    DECLINING,
    STABLE,
    FLUCTUATING
}

/**
 * Statistical summary of mood data
 */
data class MoodStatistics(
    val totalEntries: Int,
    val averageMood: Double,
    val moodTrend: TrendDirection,
    val bestMood: Int,
    val worstMood: Int,
    val mostCommonEmotions: List<String>,
    val currentStreak: Int,
    val longestStreak: Int,
    val improvementPercentage: Double,
    val consistencyScore: Double, // 0.0 to 1.0
    val period: String
)

/**
 * Mood trend data for charting
 */
data class MoodTrendData(
    val date: LocalDateTime,
    val averageMood: Double,
    val entryCount: Int,
    val dominantEmotion: String?
)

/**
 * Emotion analysis data
 */
data class EmotionAnalysis(
    val emotion: String,
    val frequency: Int,
    val percentage: Double,
    val averageMoodWhenPresent: Double,
    val trend: TrendDirection
)

/**
 * Activity correlation data
 */
data class ActivityCorrelation(
    val activity: String,
    val occurrences: Int,
    val averageMood: Double,
    val moodImpact: MoodImpact
)

/**
 * Impact of activities on mood
 */
enum class MoodImpact {
    VERY_POSITIVE,
    POSITIVE,
    NEUTRAL,
    NEGATIVE,
    VERY_NEGATIVE
}

/**
 * Weekly summary data
 */
data class WeeklySummary(
    val weekStart: LocalDateTime,
    val weekEnd: LocalDateTime,
    val averageMood: Double,
    val totalEntries: Int,
    val streak: Int,
    val topEmotions: List<String>,
    val moodTrend: TrendDirection,
    val insights: List<Insight>
)

/**
 * Chart data point for mood visualization
 */
data class ChartDataPoint(
    val x: Float,
    val y: Float,
    val date: LocalDateTime,
    val value: Double,
    val label: String? = null
)
