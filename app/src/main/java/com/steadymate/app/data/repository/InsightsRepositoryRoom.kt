package com.steadymate.app.data.repository

import com.steadymate.app.data.database.dao.MoodEntryDao
import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.InsightsRepository
import com.steadymate.app.domain.repository.TimeRange
import com.steadymate.app.domain.usecase.insights.GetEmotionAnalysisUseCase
import com.steadymate.app.domain.usecase.insights.GetMoodStatisticsUseCase
import com.steadymate.app.domain.usecase.insights.GetMoodTrendUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.random.Random

/**
 * Room-backed implementation of InsightsRepository.
 * Provides real data analysis from the local database.
 */
@Singleton
class InsightsRepositoryRoom @Inject constructor(
    private val moodEntryDao: MoodEntryDao,
    private val getMoodTrendUseCase: GetMoodTrendUseCase,
    private val getEmotionAnalysisUseCase: GetEmotionAnalysisUseCase,
    private val getMoodStatisticsUseCase: GetMoodStatisticsUseCase
) : InsightsRepository {
    
    override suspend fun generateInsights(userId: String, timeRange: TimeRange): List<Insight> {
        val insights = mutableListOf<Insight>()
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        // Generate mood trend insight
        val moodStats = getMoodStatisticsUseCase.execute(userId, timeRange)
        if (moodStats != null) {
            val trendInsight = generateMoodTrendInsight(moodStats, currentTime)
            insights.add(trendInsight)
            
            // Generate streak insight if significant
            if (moodStats.currentStreak >= 3) {
                val streakInsight = generateStreakInsight(moodStats, currentTime)
                insights.add(streakInsight)
            }
            
            // Generate consistency insight
            if (moodStats.consistencyScore > 0) {
                val consistencyInsight = generateConsistencyInsight(moodStats, currentTime)
                insights.add(consistencyInsight)
            }
        }
        
        // Generate emotion pattern insights
        val emotionAnalysis = getEmotionAnalysisUseCase.execute(userId, timeRange)
        if (emotionAnalysis.isNotEmpty()) {
            val emotionInsight = generateEmotionInsight(emotionAnalysis, currentTime)
            insights.add(emotionInsight)
        }
        
        return insights.take(5) // Limit to top 5 insights
    }
    
    override suspend fun getMoodStatistics(userId: String, timeRange: TimeRange): MoodStatistics {
        return getMoodStatisticsUseCase.execute(userId, timeRange) ?: createEmptyStatistics(timeRange)
    }
    
    override suspend fun getMoodTrendData(userId: String, timeRange: TimeRange): List<MoodTrendData> {
        return getMoodTrendUseCase.execute(userId, timeRange)
    }
    
    override fun getMoodTrendDataFlow(userId: String, timeRange: TimeRange): Flow<List<MoodTrendData>> {
        return getMoodTrendUseCase.executeFlow(userId, timeRange)
    }
    
    override suspend fun analyzeEmotions(userId: String, timeRange: TimeRange): List<EmotionAnalysis> {
        return getEmotionAnalysisUseCase.execute(userId, timeRange)
    }
    
    override suspend fun analyzeActivityCorrelations(userId: String, timeRange: TimeRange): List<ActivityCorrelation> {
        // TODO: Implement when activity tracking is added
        // For now, return sample data based on notes analysis
        return generateActivityCorrelationsFromNotes(userId, timeRange)
    }
    
    override suspend fun getWeeklySummary(userId: String, weekStart: LocalDateTime): WeeklySummary {
        val weekEnd = weekStart.toInstant(TimeZone.currentSystemDefault()).plus(DateTimePeriod(days = 7), TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
        val startTime = weekStart.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endTime = weekEnd.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        
        val stats = moodEntryDao.getMoodStatisticsForPeriod(userId, startTime, endTime)
        val topEmotions = getEmotionAnalysisUseCase.getTopEmotions(userId, TimeRange.WEEK, 3)
        
        return WeeklySummary(
            weekStart = weekStart,
            weekEnd = weekEnd,
            averageMood = stats?.averageMood ?: 0.0,
            totalEntries = stats?.totalEntries ?: 0,
            streak = calculateCurrentStreak(userId),
            topEmotions = topEmotions,
            moodTrend = calculateTrendFromImprovement(calculateMoodImprovement(userId, TimeRange.WEEK)),
            insights = generateInsights(userId, TimeRange.WEEK).take(3)
        )
    }
    
    override suspend fun calculateCurrentStreak(userId: String): Int {
        return getMoodStatisticsUseCase.execute(userId, TimeRange.MONTH)?.currentStreak ?: 0
    }
    
    override suspend fun calculateLongestStreak(userId: String): Int {
        return getMoodStatisticsUseCase.execute(userId, TimeRange.YEAR)?.longestStreak ?: 0
    }
    
    override suspend fun getChartData(userId: String, timeRange: TimeRange): List<ChartDataPoint> {
        return getMoodTrendUseCase.getChartDataPoints(userId, timeRange)
    }
    
    override suspend fun detectMoodPatterns(userId: String, timeRange: TimeRange): List<Insight> {
        // Advanced pattern detection - placeholder for ML features
        val insights = mutableListOf<Insight>()
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        // Detect weekend vs weekday patterns, morning vs evening, etc.
        val trendData = getMoodTrendUseCase.execute(userId, timeRange)
        if (trendData.size >= 7) {
            // Simple volatility detection
            val moodValues = trendData.map { it.averageMood }
            val average = moodValues.average()
            val variance = moodValues.map { (it - average) * (it - average) }.average()
            
            if (variance > 1.0) { // High volatility
                insights.add(
                    Insight(
                        id = "volatility_${System.currentTimeMillis()}",
                        type = InsightType.WARNING_SIGN,
                        title = "Mood Fluctuation",
                        description = "Your mood has been fluctuating more than usual",
                        value = String.format("%.1f", variance),
                        trend = TrendDirection.FLUCTUATING,
                        confidence = 0.7f,
                        generatedAt = currentTime,
                        actionable = true,
                        recommendation = "Consider tracking what activities or events precede mood changes"
                    )
                )
            }
        }
        
        return insights
    }
    
    override suspend fun calculateMoodImprovement(userId: String, timeRange: TimeRange): Double {
        return getMoodStatisticsUseCase.execute(userId, timeRange)?.improvementPercentage ?: 0.0
    }
    
    override suspend fun calculateConsistencyScore(userId: String, timeRange: TimeRange): Double {
        return getMoodStatisticsUseCase.execute(userId, timeRange)?.consistencyScore ?: 0.0
    }
    
    // Private helper methods
    
    private fun generateMoodTrendInsight(stats: MoodStatistics, currentTime: LocalDateTime): Insight {
        val improvement = stats.improvementPercentage
        
        return when {
            improvement > 10 -> Insight(
                id = "trend_positive_${System.currentTimeMillis()}",
                type = InsightType.POSITIVE_PATTERN,
                title = "Great Progress! 🎉",
                description = "Your mood has improved significantly this ${stats.period}",
                value = "${String.format("%.1f", improvement)}%",
                trend = TrendDirection.IMPROVING,
                confidence = 0.9f,
                generatedAt = currentTime,
                actionable = false,
                recommendation = "Keep doing what's working for you!"
            )
            improvement < -10 -> Insight(
                id = "trend_concern_${System.currentTimeMillis()}",
                type = InsightType.WARNING_SIGN,
                title = "Mood Decline Noticed",
                description = "Your mood has decreased this ${stats.period}",
                value = "${String.format("%.1f", improvement)}%",
                trend = TrendDirection.DECLINING,
                confidence = 0.8f,
                generatedAt = currentTime,
                actionable = true,
                recommendation = "Consider reaching out to support or trying mood-boosting activities"
            )
            else -> Insight(
                id = "trend_stable_${System.currentTimeMillis()}",
                type = InsightType.MOOD_TREND,
                title = "Steady Progress",
                description = "Your mood has remained stable this ${stats.period}",
                value = String.format("%.1f", stats.averageMood),
                trend = TrendDirection.STABLE,
                confidence = 0.7f,
                generatedAt = currentTime
            )
        }
    }
    
    private fun generateStreakInsight(stats: MoodStatistics, currentTime: LocalDateTime): Insight {
        return Insight(
            id = "streak_${System.currentTimeMillis()}",
            type = InsightType.STREAK_ANALYSIS,
            title = "Consistency Streak! 🔥",
            description = "You've logged your mood for ${stats.currentStreak} days in a row",
            value = "${stats.currentStreak} days",
            trend = TrendDirection.STABLE,
            confidence = 1.0f,
            generatedAt = currentTime,
            actionable = false,
            recommendation = "Great habit! Keep it going."
        )
    }
    
    private fun generateConsistencyInsight(stats: MoodStatistics, currentTime: LocalDateTime): Insight {
        val score = stats.consistencyScore
        return when {
            score >= 80 -> Insight(
                id = "consistency_high_${System.currentTimeMillis()}",
                type = InsightType.POSITIVE_PATTERN,
                title = "Excellent Tracking",
                description = "You've been very consistent with mood logging",
                value = "${score.toInt()}%",
                trend = TrendDirection.STABLE,
                confidence = 0.9f,
                generatedAt = currentTime
            )
            score >= 50 -> Insight(
                id = "consistency_medium_${System.currentTimeMillis()}",
                type = InsightType.IMPROVEMENT_OPPORTUNITY,
                title = "Good Consistency",
                description = "You're tracking regularly - room for improvement",
                value = "${score.toInt()}%",
                trend = TrendDirection.IMPROVING,
                confidence = 0.7f,
                generatedAt = currentTime,
                actionable = true,
                recommendation = "Try setting a daily reminder to track your mood"
            )
            else -> Insight(
                id = "consistency_low_${System.currentTimeMillis()}",
                type = InsightType.IMPROVEMENT_OPPORTUNITY,
                title = "Tracking Opportunity",
                description = "More regular tracking will provide better insights",
                value = "${score.toInt()}%",
                trend = TrendDirection.DECLINING,
                confidence = 0.6f,
                generatedAt = currentTime,
                actionable = true,
                recommendation = "Set up daily notifications to build a tracking habit"
            )
        }
    }
    
    private fun generateEmotionInsight(emotions: List<EmotionAnalysis>, currentTime: LocalDateTime): Insight {
        val topEmotion = emotions.first()
        return Insight(
            id = "emotion_${System.currentTimeMillis()}",
            type = InsightType.EMOTION_PATTERN,
            title = "Top Emotion: ${topEmotion.emotion}",
            description = "Your most frequent emotion accounts for ${topEmotion.percentage.toInt()}% of entries",
            value = "${topEmotion.frequency} times",
            trend = topEmotion.trend,
            confidence = 0.8f,
            generatedAt = currentTime,
            actionable = topEmotion.emotion.lowercase() in listOf("sad", "anxious", "angry", "stressed"),
            recommendation = if (topEmotion.emotion.lowercase() in listOf("sad", "anxious", "angry", "stressed")) {
                "Consider exploring coping strategies for managing ${topEmotion.emotion.lowercase()} feelings"
            } else null
        )
    }
    
    private suspend fun generateActivityCorrelationsFromNotes(userId: String, timeRange: TimeRange): List<ActivityCorrelation> {
        // Simple activity correlation based on common keywords in notes
        val (startTime, endTime) = getTimeRangeMillis(timeRange)
        val entries = moodEntryDao.getMoodEntriesInRange(startTime, endTime)
            .filter { it.userId == userId && it.notes.isNotBlank() }
        
        val activityKeywords = mapOf(
            "exercise" to listOf("exercise", "gym", "run", "walk", "workout", "jog"),
            "work" to listOf("work", "job", "office", "meeting", "deadline", "project"),
            "sleep" to listOf("sleep", "tired", "rest", "bed", "nap"),
            "social" to listOf("friends", "family", "social", "party", "date", "visit"),
            "stress" to listOf("stress", "pressure", "overwhelm", "anxiety", "worry")
        )
        
        return activityKeywords.mapNotNull { (activity, keywords) ->
            val relatedEntries = entries.filter { entry ->
                keywords.any { keyword ->
                    entry.notes.contains(keyword, ignoreCase = true)
                }
            }
            
            if (relatedEntries.isNotEmpty()) {
                val avgMood = relatedEntries.map { it.moodLevel }.average()
                val impact = when {
                    avgMood >= 7 -> MoodImpact.VERY_POSITIVE
                    avgMood >= 5 -> MoodImpact.POSITIVE
                    avgMood >= 4 -> MoodImpact.NEUTRAL
                    avgMood >= 2 -> MoodImpact.NEGATIVE
                    else -> MoodImpact.VERY_NEGATIVE
                }
                
                ActivityCorrelation(
                    activity = activity.replaceFirstChar { it.uppercase() },
                    occurrences = relatedEntries.size,
                    averageMood = avgMood,
                    moodImpact = impact
                )
            } else null
        }.sortedByDescending { it.occurrences }
    }
    
    private fun createEmptyStatistics(timeRange: TimeRange): MoodStatistics {
        return MoodStatistics(
            totalEntries = 0,
            averageMood = 0.0,
            moodTrend = TrendDirection.STABLE,
            bestMood = 0,
            worstMood = 0,
            mostCommonEmotions = emptyList(),
            currentStreak = 0,
            longestStreak = 0,
            improvementPercentage = 0.0,
            consistencyScore = 0.0,
            period = formatPeriodName(timeRange)
        )
    }
    
    private fun calculateTrendFromImprovement(improvement: Double): TrendDirection {
        return when {
            improvement > 5.0 -> TrendDirection.IMPROVING
            improvement < -5.0 -> TrendDirection.DECLINING
            abs(improvement) <= 5.0 -> TrendDirection.STABLE
            else -> TrendDirection.FLUCTUATING
        }
    }
    
    private fun formatPeriodName(timeRange: TimeRange): String {
        return when (timeRange) {
            TimeRange.WEEK -> "week"
            TimeRange.MONTH -> "month"
            TimeRange.THREE_MONTHS -> "3 months"
            TimeRange.SIX_MONTHS -> "6 months"
            TimeRange.YEAR -> "year"
        }
    }
    
    private fun getTimeRangeMillis(timeRange: TimeRange): Pair<Long, Long> {
        val now = Clock.System.now()
        val endTime = now.toEpochMilliseconds()
        val startTime = now.minus(DateTimePeriod(days = timeRange.days), TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return Pair(startTime, endTime)
    }
}
