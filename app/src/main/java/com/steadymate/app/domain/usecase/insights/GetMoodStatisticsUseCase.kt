package com.steadymate.app.domain.usecase.insights

import com.steadymate.app.data.database.dao.MoodEntryDao
import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.TimeRange
import kotlinx.datetime.*
import javax.inject.Inject
import kotlin.math.abs

/**
 * Use case for calculating comprehensive mood statistics
 */
class GetMoodStatisticsUseCase @Inject constructor(
    private val moodEntryDao: MoodEntryDao
) {
    
    suspend fun execute(userId: String, timeRange: TimeRange): MoodStatistics? {
        val (startTime, endTime) = getTimeRangeMillis(timeRange)
        val stats = moodEntryDao.getMoodStatisticsForPeriod(userId, startTime, endTime) 
            ?: return null
        
        if (stats.totalEntries == 0) return null
        
        val currentStreak = calculateCurrentStreak(userId)
        val longestStreak = calculateLongestStreak(userId)
        val improvementPercentage = calculateMoodImprovement(userId, timeRange)
        val consistencyScore = calculateConsistencyScore(userId, timeRange)
        val moodTrend = calculateMoodTrend(userId, timeRange)
        val topEmotions = getTopEmotions(userId, timeRange, 3)
        
        return MoodStatistics(
            totalEntries = stats.totalEntries,
            averageMood = stats.averageMood,
            moodTrend = moodTrend,
            bestMood = stats.maxMood,
            worstMood = stats.minMood,
            mostCommonEmotions = topEmotions,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            improvementPercentage = improvementPercentage,
            consistencyScore = consistencyScore,
            period = formatPeriodName(timeRange)
        )
    }
    
    private suspend fun calculateCurrentStreak(userId: String): Int {
        val recent = moodEntryDao.getMostRecentEntry(userId) ?: return 0
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val recentDateTime = kotlinx.datetime.Instant.fromEpochMilliseconds(recent.timestamp)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        val recentDate = recentDateTime.date
        
        // If last entry is not today or yesterday, streak is broken
        if (recentDate != today && recentDate != today.minus(1, DateTimeUnit.DAY)) {
            return 0
        }
        
        // Count consecutive days with entries going backwards
        var currentDate = today
        var streak = 0
        val thirtyDaysAgo = today.minus(30, DateTimeUnit.DAY)
        
        while (currentDate >= thirtyDaysAgo) {
            val dayStart = currentDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            val dayEnd = currentDate.plus(1, DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            
            val entries = moodEntryDao.getMoodEntriesInRange(dayStart, dayEnd)
            if (entries.any { it.userId == userId }) {
                streak++
                currentDate = currentDate.minus(1, DateTimeUnit.DAY)
            } else {
                break
            }
        }
        
        return streak
    }
    
    private suspend fun calculateLongestStreak(userId: String): Int {
        val daysWithEntries = moodEntryDao.getDaysWithEntries(
            userId,
            Clock.System.now().minus(DateTimePeriod(days = 365), TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            Clock.System.now().toEpochMilliseconds()
        )
        
        if (daysWithEntries.isEmpty()) return 0
        
        val sortedDates = daysWithEntries.map { LocalDate.parse(it) }.sorted()
        var maxStreak = 1
        var currentStreak = 1
        
        for (i in 1 until sortedDates.size) {
            val previous = sortedDates[i - 1]
            val current = sortedDates[i]
            
            if (current == previous.plus(1, DateTimeUnit.DAY)) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }
        
        return maxStreak
    }
    
    private suspend fun calculateMoodImprovement(userId: String, timeRange: TimeRange): Double {
        val now = Clock.System.now()
        val midPoint = now.minus(DateTimePeriod(days = timeRange.days / 2), TimeZone.currentSystemDefault())
        
        val earlierPeriodStart = now.minus(DateTimePeriod(days = timeRange.days), TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val earlierPeriodEnd = midPoint.toEpochMilliseconds()
        val laterPeriodStart = midPoint.toEpochMilliseconds()
        val laterPeriodEnd = now.toEpochMilliseconds()
        
        val earlierStats = moodEntryDao.getMoodStatisticsForPeriod(userId, earlierPeriodStart, earlierPeriodEnd)
        val laterStats = moodEntryDao.getMoodStatisticsForPeriod(userId, laterPeriodStart, laterPeriodEnd)
        
        return if (earlierStats != null && laterStats != null && earlierStats.averageMood > 0) {
            ((laterStats.averageMood - earlierStats.averageMood) / earlierStats.averageMood) * 100.0
        } else {
            0.0
        }
    }
    
    private suspend fun calculateConsistencyScore(userId: String, timeRange: TimeRange): Double {
        val (startTime, endTime) = getTimeRangeMillis(timeRange)
        val totalDays = timeRange.days
        val daysWithEntries = moodEntryDao.getDaysWithEntries(userId, startTime, endTime).size
        
        return if (totalDays > 0) (daysWithEntries.toDouble() / totalDays) * 100.0 else 0.0
    }
    
    private suspend fun calculateMoodTrend(userId: String, timeRange: TimeRange): TrendDirection {
        val improvementPercentage = calculateMoodImprovement(userId, timeRange)
        
        return when {
            improvementPercentage > 5.0 -> TrendDirection.IMPROVING
            improvementPercentage < -5.0 -> TrendDirection.DECLINING
            abs(improvementPercentage) <= 5.0 -> TrendDirection.STABLE
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
    
    private suspend fun getTopEmotions(userId: String, timeRange: TimeRange, limit: Int): List<String> {
        val (startTime, endTime) = getTimeRangeMillis(timeRange)
        val emotionFrequencies = moodEntryDao.getEmotionFrequencies(userId, startTime, endTime)
        return emotionFrequencies.take(limit).map { it.emotion }
    }
    
    private fun getTimeRangeMillis(timeRange: TimeRange): Pair<Long, Long> {
        val now = Clock.System.now()
        val endTime = now.toEpochMilliseconds()
        val startTime = now.minus(DateTimePeriod(days = timeRange.days), TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return Pair(startTime, endTime)
    }
}
