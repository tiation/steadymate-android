package com.steadymate.app.domain.repository

import com.steadymate.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * Repository interface for insights and analytics operations.
 * Provides mood data analysis and trend calculations.
 */
interface InsightsRepository {
    
    /**
     * Generate insights for a user based on their mood data
     */
    suspend fun generateInsights(userId: String, timeRange: TimeRange): List<Insight>
    
    /**
     * Get mood statistics for a time period
     */
    suspend fun getMoodStatistics(userId: String, timeRange: TimeRange): MoodStatistics
    
    /**
     * Get mood trend data for charting
     */
    suspend fun getMoodTrendData(userId: String, timeRange: TimeRange): List<MoodTrendData>
    
    /**
     * Get mood trend data as a Flow for reactive updates
     */
    fun getMoodTrendDataFlow(userId: String, timeRange: TimeRange): Flow<List<MoodTrendData>>
    
    /**
     * Analyze emotion patterns and frequencies
     */
    suspend fun analyzeEmotions(userId: String, timeRange: TimeRange): List<EmotionAnalysis>
    
    /**
     * Analyze activity correlations with mood
     */
    suspend fun analyzeActivityCorrelations(userId: String, timeRange: TimeRange): List<ActivityCorrelation>
    
    /**
     * Get weekly summary data
     */
    suspend fun getWeeklySummary(userId: String, weekStart: LocalDateTime): WeeklySummary
    
    /**
     * Calculate current streak
     */
    suspend fun calculateCurrentStreak(userId: String): Int
    
    /**
     * Calculate longest streak
     */
    suspend fun calculateLongestStreak(userId: String): Int
    
    /**
     * Get chart data points for visualization
     */
    suspend fun getChartData(userId: String, timeRange: TimeRange): List<ChartDataPoint>
    
    /**
     * Check for mood patterns and anomalies
     */
    suspend fun detectMoodPatterns(userId: String, timeRange: TimeRange): List<Insight>
    
    /**
     * Calculate mood improvement percentage
     */
    suspend fun calculateMoodImprovement(userId: String, timeRange: TimeRange): Double
    
    /**
     * Calculate consistency score (how regular mood tracking is)
     */
    suspend fun calculateConsistencyScore(userId: String, timeRange: TimeRange): Double
}

/**
 * Time range enum for insights analysis
 */
enum class TimeRange(val days: Int) {
    WEEK(7),
    MONTH(30),
    THREE_MONTHS(90),
    SIX_MONTHS(180),
    YEAR(365)
}
