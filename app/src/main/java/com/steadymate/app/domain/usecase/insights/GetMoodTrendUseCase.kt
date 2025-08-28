package com.steadymate.app.domain.usecase.insights

import com.steadymate.app.data.database.dao.MoodEntryDao
import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.TimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * Use case for getting mood trend data for insights and charting
 */
class GetMoodTrendUseCase @Inject constructor(
    private val moodEntryDao: MoodEntryDao
) {
    
    suspend fun execute(userId: String, timeRange: TimeRange): List<MoodTrendData> {
        val (startTime, endTime) = getTimeRangeMillis(timeRange)
        val results = moodEntryDao.getDailyMoodAggregates(userId, startTime, endTime)
        
        return results.map { result ->
            MoodTrendData(
                date = parseDate(result.date),
                averageMood = result.averageMood,
                entryCount = result.entryCount,
                dominantEmotion = null // Will be enhanced later with emotion analysis
            )
        }
    }
    
    fun executeFlow(userId: String, timeRange: TimeRange): Flow<List<MoodTrendData>> {
        val (startTime, endTime) = getTimeRangeMillis(timeRange)
        return moodEntryDao.getMoodTrendFlow(userId, startTime, endTime).map { results ->
            results.map { result ->
                MoodTrendData(
                    date = parseDate(result.date),
                    averageMood = result.averageMood,
                    entryCount = result.entryCount,
                    dominantEmotion = null
                )
            }
        }
    }
    
    suspend fun getChartDataPoints(userId: String, timeRange: TimeRange): List<ChartDataPoint> {
        val trendData = execute(userId, timeRange)
        return trendData.mapIndexed { index, data ->
            ChartDataPoint(
                x = index.toFloat(),
                y = data.averageMood.toFloat(),
                date = data.date,
                value = data.averageMood,
                label = formatDateLabel(data.date, timeRange)
            )
        }
    }
    
    private fun getTimeRangeMillis(timeRange: TimeRange): Pair<Long, Long> {
        val now = Clock.System.now()
        val endTime = now.toEpochMilliseconds()
        val startTime = now.minus(DateTimePeriod(days = timeRange.days), TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return Pair(startTime, endTime)
    }
    
    private fun parseDate(dateString: String): LocalDateTime {
        // Parse "YYYY-MM-DD" format from SQLite date() function
        val parts = dateString.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        return LocalDate(year, month, day).atStartOfDayIn(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
    }
    
    private fun formatDateLabel(date: LocalDateTime, timeRange: TimeRange): String {
        return when (timeRange) {
            TimeRange.WEEK -> "${date.monthNumber}/${date.dayOfMonth}"
            TimeRange.MONTH -> "${date.monthNumber}/${date.dayOfMonth}"
            TimeRange.THREE_MONTHS, TimeRange.SIX_MONTHS -> "${date.monthNumber}/${date.dayOfMonth}"
            TimeRange.YEAR -> "${date.monthNumber}/${date.dayOfMonth}"
        }
    }
}
