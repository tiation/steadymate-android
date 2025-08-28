package com.steadymate.app.data.database.dao

import androidx.room.*
import com.steadymate.app.data.database.entities.MoodEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * Data Access Object for MoodEntry entity operations.
 * Provides database operations for mood tracking data in the SteadyMate app.
 */
@Dao
interface MoodEntryDao {
    
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntriesFlow(): Flow<List<MoodEntryEntity>>
    
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    suspend fun getAllMoodEntries(): List<MoodEntryEntity>
    
    @Query("SELECT * FROM mood_entries WHERE id = :entryId")
    suspend fun getMoodEntryById(entryId: String): MoodEntryEntity?
    
    @Query("SELECT * FROM mood_entries WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getMoodEntriesInRange(startTime: Long, endTime: Long): List<MoodEntryEntity>
    
    @Query("SELECT * FROM mood_entries WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getMoodEntriesInRangeFlow(startTime: Long, endTime: Long): Flow<List<MoodEntryEntity>>
    
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMoodEntries(limit: Int): List<MoodEntryEntity>
    
    @Query("SELECT AVG(moodLevel) FROM mood_entries WHERE timestamp >= :startTime")
    suspend fun getAverageMoodSince(startTime: Long): Double?
    
    @Query("SELECT * FROM mood_entries WHERE moodLevel >= :minScore AND moodLevel <= :maxScore ORDER BY timestamp DESC")
    suspend fun getMoodEntriesByScoreRange(minScore: Int, maxScore: Int): List<MoodEntryEntity>
    
    @Query("SELECT COUNT(*) FROM mood_entries")
    suspend fun getMoodEntryCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(moodEntry: MoodEntryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntries(moodEntries: List<MoodEntryEntity>)
    
    @Update
    suspend fun updateMoodEntry(moodEntry: MoodEntryEntity)
    
    @Delete
    suspend fun deleteMoodEntry(moodEntry: MoodEntryEntity)
    
    @Query("DELETE FROM mood_entries WHERE id = :entryId")
    suspend fun deleteMoodEntryById(entryId: String)
    
    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllMoodEntries()
    
    @Query("DELETE FROM mood_entries WHERE timestamp < :beforeTime")
    suspend fun deleteMoodEntriesBefore(beforeTime: Long)
    
    // ====================
    // INSIGHTS QUERIES
    // ====================
    
    /**
     * Get daily mood aggregates for insights
     */
    @Query("""
        SELECT 
            date(datetime(timestamp / 1000, 'unixepoch')) as date,
            AVG(moodLevel * 1.0) as averageMood,
            COUNT(*) as entryCount,
            MIN(moodLevel) as minMood,
            MAX(moodLevel) as maxMood
        FROM mood_entries 
        WHERE userId = :userId 
            AND timestamp >= :startTime 
            AND timestamp <= :endTime
        GROUP BY date(datetime(timestamp / 1000, 'unixepoch'))
        ORDER BY date
    """)
    suspend fun getDailyMoodAggregates(userId: String, startTime: Long, endTime: Long): List<DailyMoodAggregateResult>
    
    /**
     * Get emotion frequency data for analysis
     */
    @Query("""
        SELECT 
            json_each.value as emotion,
            COUNT(*) as frequency,
            AVG(moodLevel * 1.0) as averageMood
        FROM mood_entries, json_each(emotionTags)
        WHERE userId = :userId 
            AND timestamp >= :startTime 
            AND timestamp <= :endTime
            AND json_each.value != ''
        GROUP BY json_each.value
        ORDER BY frequency DESC
    """)
    suspend fun getEmotionFrequencies(userId: String, startTime: Long, endTime: Long): List<EmotionFrequencyResult>
    
    /**
     * Get mood statistics for a time period
     */
    @Query("""
        SELECT 
            COUNT(*) as totalEntries,
            AVG(moodLevel * 1.0) as averageMood,
            MIN(moodLevel) as minMood,
            MAX(moodLevel) as maxMood
        FROM mood_entries 
        WHERE userId = :userId 
            AND timestamp >= :startTime 
            AND timestamp <= :endTime
    """)
    suspend fun getMoodStatisticsForPeriod(userId: String, startTime: Long, endTime: Long): MoodStatisticsResult?
    
    /**
     * Get streak calculation data (days with entries)
     */
    @Query("""
        SELECT DISTINCT date(datetime(timestamp / 1000, 'unixepoch')) as date
        FROM mood_entries 
        WHERE userId = :userId 
            AND timestamp >= :startTime 
            AND timestamp <= :endTime
        ORDER BY date DESC
    """)
    suspend fun getDaysWithEntries(userId: String, startTime: Long, endTime: Long): List<String>
    
    /**
     * Get mood trend data for charting with Flow
     */
    @Query("""
        SELECT 
            date(datetime(timestamp / 1000, 'unixepoch')) as date,
            AVG(moodLevel * 1.0) as averageMood,
            COUNT(*) as entryCount
        FROM mood_entries 
        WHERE userId = :userId 
            AND timestamp >= :startTime 
            AND timestamp <= :endTime
        GROUP BY date(datetime(timestamp / 1000, 'unixepoch'))
        ORDER BY date
    """)
    fun getMoodTrendFlow(userId: String, startTime: Long, endTime: Long): Flow<List<MoodTrendResult>>
    
    /**
     * Get most recent mood entry for streak calculation
     */
    @Query("""
        SELECT * FROM mood_entries 
        WHERE userId = :userId 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getMostRecentEntry(userId: String): MoodEntryEntity?
}

// Result data classes for Room queries
data class DailyMoodAggregateResult(
    val date: String,
    val averageMood: Double,
    val entryCount: Int,
    val minMood: Int,
    val maxMood: Int
)

data class EmotionFrequencyResult(
    val emotion: String,
    val frequency: Int,
    val averageMood: Double
)

data class MoodStatisticsResult(
    val totalEntries: Int,
    val averageMood: Double,
    val minMood: Int,
    val maxMood: Int
)

data class MoodTrendResult(
    val date: String,
    val averageMood: Double,
    val entryCount: Int
)
