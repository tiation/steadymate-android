package com.steadymate.app.data.repository

import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.InsightsRepository
import com.steadymate.app.domain.repository.TimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of InsightsRepository for development.
 * Provides dummy data for insights and analytics.
 */
@Singleton
class InsightsRepositoryImpl @Inject constructor() : InsightsRepository {
    
    override suspend fun generateInsights(userId: String, timeRange: TimeRange): List<Insight> {
        return listOf(
            Insight(
                id = "1",
                type = InsightType.MOOD_TREND,
                title = "Mood Improvement",
                description = "Your mood has improved by 15% this week",
                value = "15%",
                trend = TrendDirection.IMPROVING,
                confidence = 0.85f,
                generatedAt = LocalDateTime(2024, 1, 15, 10, 0)
            ),
            Insight(
                id = "2",
                type = InsightType.POSITIVE_PATTERN,
                title = "Consistency",
                description = "Great job tracking your mood daily!",
                value = "7 days",
                trend = TrendDirection.STABLE,
                confidence = 0.90f,
                generatedAt = LocalDateTime(2024, 1, 14, 9, 0)
            )
        )
    }
    
    override suspend fun getMoodStatistics(userId: String, timeRange: TimeRange): MoodStatistics {
        return MoodStatistics(
            totalEntries = 28,
            averageMood = 4.2,
            moodTrend = TrendDirection.IMPROVING,
            bestMood = 5,
            worstMood = 2,
            mostCommonEmotions = listOf("Happy", "Calm", "Content"),
            currentStreak = 5,
            longestStreak = 28,
            improvementPercentage = 15.0,
            consistencyScore = 0.85,
            period = "This week"
        )
    }
    
    override suspend fun getMoodTrendData(userId: String, timeRange: TimeRange): List<MoodTrendData> {
        return listOf(
            MoodTrendData(date = LocalDateTime(2024, 1, 1, 0, 0), averageMood = 3.5, entryCount = 2, dominantEmotion = "Happy"),
            MoodTrendData(date = LocalDateTime(2024, 1, 2, 0, 0), averageMood = 4.0, entryCount = 3, dominantEmotion = "Calm"),
            MoodTrendData(date = LocalDateTime(2024, 1, 3, 0, 0), averageMood = 3.8, entryCount = 1, dominantEmotion = "Content"),
            MoodTrendData(date = LocalDateTime(2024, 1, 4, 0, 0), averageMood = 4.2, entryCount = 2, dominantEmotion = "Happy"),
            MoodTrendData(date = LocalDateTime(2024, 1, 5, 0, 0), averageMood = 4.5, entryCount = 4, dominantEmotion = "Energetic")
        )
    }
    
    override fun getMoodTrendDataFlow(userId: String, timeRange: TimeRange): Flow<List<MoodTrendData>> {
        return flowOf(
            listOf(
                MoodTrendData(date = LocalDateTime(2024, 1, 1, 0, 0), averageMood = 3.5, entryCount = 2, dominantEmotion = "Happy"),
                MoodTrendData(date = LocalDateTime(2024, 1, 2, 0, 0), averageMood = 4.0, entryCount = 3, dominantEmotion = "Calm"),
                MoodTrendData(date = LocalDateTime(2024, 1, 3, 0, 0), averageMood = 3.8, entryCount = 1, dominantEmotion = "Content"),
                MoodTrendData(date = LocalDateTime(2024, 1, 4, 0, 0), averageMood = 4.2, entryCount = 2, dominantEmotion = "Happy"),
                MoodTrendData(date = LocalDateTime(2024, 1, 5, 0, 0), averageMood = 4.5, entryCount = 4, dominantEmotion = "Energetic")
            )
        )
    }
    
    override suspend fun analyzeEmotions(userId: String, timeRange: TimeRange): List<EmotionAnalysis> {
        return listOf(
            EmotionAnalysis(emotion = "Happy", frequency = 12, percentage = 40.0, averageMoodWhenPresent = 4.5, trend = TrendDirection.STABLE),
            EmotionAnalysis(emotion = "Calm", frequency = 8, percentage = 27.0, averageMoodWhenPresent = 4.2, trend = TrendDirection.IMPROVING),
            EmotionAnalysis(emotion = "Anxious", frequency = 5, percentage = 17.0, averageMoodWhenPresent = 2.8, trend = TrendDirection.DECLINING),
            EmotionAnalysis(emotion = "Sad", frequency = 3, percentage = 10.0, averageMoodWhenPresent = 2.1, trend = TrendDirection.STABLE),
            EmotionAnalysis(emotion = "Angry", frequency = 2, percentage = 6.0, averageMoodWhenPresent = 2.0, trend = TrendDirection.STABLE)
        )
    }
    
    override suspend fun analyzeActivityCorrelations(userId: String, timeRange: TimeRange): List<ActivityCorrelation> {
        return listOf(
            ActivityCorrelation(activity = "Exercise", occurrences = 10, averageMood = 4.5, moodImpact = MoodImpact.POSITIVE),
            ActivityCorrelation(activity = "Sleep", occurrences = 15, averageMood = 4.2, moodImpact = MoodImpact.POSITIVE),
            ActivityCorrelation(activity = "Work", occurrences = 20, averageMood = 3.1, moodImpact = MoodImpact.NEGATIVE)
        )
    }
    
    override suspend fun getWeeklySummary(userId: String, weekStart: LocalDateTime): WeeklySummary {
        return WeeklySummary(
            weekStart = weekStart,
            weekEnd = LocalDateTime(weekStart.date.plus(DatePeriod(days = 6)), weekStart.time),
            averageMood = 4.1,
            totalEntries = 7,
            streak = 5,
            topEmotions = listOf("Happy", "Calm", "Energetic"),
            moodTrend = TrendDirection.IMPROVING,
            insights = listOf()
        )
    }
    
    override suspend fun calculateCurrentStreak(userId: String): Int {
        return 5
    }
    
    override suspend fun calculateLongestStreak(userId: String): Int {
        return 28
    }
    
    override suspend fun getChartData(userId: String, timeRange: TimeRange): List<ChartDataPoint> {
        return listOf(
            ChartDataPoint(x = 1.0f, y = 3.5f, date = LocalDateTime(2024, 1, 1, 0, 0), value = 3.5, label = "Day 1"),
            ChartDataPoint(x = 2.0f, y = 4.0f, date = LocalDateTime(2024, 1, 2, 0, 0), value = 4.0, label = "Day 2"),
            ChartDataPoint(x = 3.0f, y = 3.8f, date = LocalDateTime(2024, 1, 3, 0, 0), value = 3.8, label = "Day 3"),
            ChartDataPoint(x = 4.0f, y = 4.2f, date = LocalDateTime(2024, 1, 4, 0, 0), value = 4.2, label = "Day 4"),
            ChartDataPoint(x = 5.0f, y = 4.5f, date = LocalDateTime(2024, 1, 5, 0, 0), value = 4.5, label = "Day 5")
        )
    }
    
    override suspend fun detectMoodPatterns(userId: String, timeRange: TimeRange): List<Insight> {
        return listOf(
            Insight(
                id = "pattern_1",
                type = InsightType.EMOTION_PATTERN,
                title = "Morning Boost",
                description = "Your mood tends to be higher in the mornings",
                value = "80%",
                trend = TrendDirection.STABLE,
                confidence = 0.75f,
                generatedAt = LocalDateTime(2024, 1, 15, 8, 0)
            )
        )
    }
    
    override suspend fun calculateMoodImprovement(userId: String, timeRange: TimeRange): Double {
        return 15.0
    }
    
    override suspend fun calculateConsistencyScore(userId: String, timeRange: TimeRange): Double {
        return 85.0
    }
}

