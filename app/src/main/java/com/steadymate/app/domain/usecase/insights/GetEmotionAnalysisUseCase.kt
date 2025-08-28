package com.steadymate.app.domain.usecase.insights

import com.steadymate.app.data.database.dao.MoodEntryDao
import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.TimeRange
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import javax.inject.Inject

/**
 * Use case for analyzing emotion patterns and frequencies
 */
class GetEmotionAnalysisUseCase @Inject constructor(
    private val moodEntryDao: MoodEntryDao
) {
    
    suspend fun execute(userId: String, timeRange: TimeRange): List<EmotionAnalysis> {
        val (startTime, endTime) = getTimeRangeMillis(timeRange)
        val emotionFrequencies = moodEntryDao.getEmotionFrequencies(userId, startTime, endTime)
        val totalEmotionEntries = emotionFrequencies.sumOf { it.frequency }
        
        if (totalEmotionEntries == 0) return emptyList()
        
        return emotionFrequencies.map { emotion ->
            val percentage = (emotion.frequency.toDouble() / totalEmotionEntries) * 100.0
            val trend = calculateEmotionTrend(userId, emotion.emotion, timeRange)
            
            EmotionAnalysis(
                emotion = emotion.emotion,
                frequency = emotion.frequency,
                percentage = percentage,
                averageMoodWhenPresent = emotion.averageMood,
                trend = trend
            )
        }.sortedByDescending { it.frequency }
    }
    
    private suspend fun calculateEmotionTrend(
        userId: String, 
        emotion: String, 
        timeRange: TimeRange
    ): TrendDirection {
        // Compare current period with previous period
        val now = Clock.System.now()
        val currentPeriodStart = now.minus(DateTimePeriod(days = timeRange.days), TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val currentPeriodEnd = now.toEpochMilliseconds()
        
        val previousPeriodStart = now.minus(DateTimePeriod(days = timeRange.days * 2), TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val previousPeriodEnd = currentPeriodStart
        
        val currentFrequencies = moodEntryDao.getEmotionFrequencies(userId, currentPeriodStart, currentPeriodEnd)
        val previousFrequencies = moodEntryDao.getEmotionFrequencies(userId, previousPeriodStart, previousPeriodEnd)
        
        val currentCount = currentFrequencies.find { it.emotion == emotion }?.frequency ?: 0
        val previousCount = previousFrequencies.find { it.emotion == emotion }?.frequency ?: 0
        
        return when {
            previousCount == 0 && currentCount > 0 -> TrendDirection.IMPROVING
            currentCount == 0 && previousCount > 0 -> TrendDirection.DECLINING
            currentCount > previousCount -> TrendDirection.IMPROVING
            currentCount < previousCount -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }
    }
    
    suspend fun getTopEmotions(userId: String, timeRange: TimeRange, limit: Int = 5): List<String> {
        val analysis = execute(userId, timeRange)
        return analysis.take(limit).map { it.emotion }
    }
    
    suspend fun getDominantEmotion(userId: String, timeRange: TimeRange): String? {
        val analysis = execute(userId, timeRange)
        return analysis.firstOrNull()?.emotion
    }
    
    private fun getTimeRangeMillis(timeRange: TimeRange): Pair<Long, Long> {
        val now = Clock.System.now()
        val endTime = now.toEpochMilliseconds()
        val startTime = now.minus(DateTimePeriod(days = timeRange.days), TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return Pair(startTime, endTime)
    }
}
