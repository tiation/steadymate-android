package com.steadymate.app.domain.repository

import com.steadymate.app.domain.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * Repository interface for mood tracking operations.
 * Simplified for local-first approach - single user per device.
 */
interface MoodRepository {
    
    /**
     * Get all mood entries as a Flow (reactive stream)
     */
    fun getAllMoodEntriesFlow(): Flow<List<MoodEntry>>
    
    /**
     * Get all mood entries
     */
    suspend fun getAllMoodEntries(): List<MoodEntry>
    
    /**
     * Get a specific mood entry by ID
     */
    suspend fun getMoodEntryById(entryId: String): MoodEntry?
    
    /**
     * Get mood entries within a timestamp range
     */
    suspend fun getMoodEntriesInRange(startTime: Long, endTime: Long): List<MoodEntry>
    
    /**
     * Get mood entries within a timestamp range as a Flow
     */
    fun getMoodEntriesInRangeFlow(startTime: Long, endTime: Long): Flow<List<MoodEntry>>
    
    /**
     * Get recent mood entries (limited number)
     */
    suspend fun getRecentMoodEntries(limit: Int): List<MoodEntry>
    
    /**
     * Get average mood score since a specific timestamp
     */
    suspend fun getAverageMoodSince(startTime: Long): Double?
    
    /**
     * Get mood entries within a specific score range (0-10)
     */
    suspend fun getMoodEntriesByScoreRange(minScore: Int, maxScore: Int): List<MoodEntry>
    
    /**
     * Get total count of mood entries
     */
    suspend fun getMoodEntryCount(): Int
    
    /**
     * Insert a new mood entry
     */
    suspend fun insertMoodEntry(moodEntry: MoodEntry)
    
    /**
     * Insert multiple mood entries
     */
    suspend fun insertMoodEntries(moodEntries: List<MoodEntry>)
    
    /**
     * Update an existing mood entry
     */
    suspend fun updateMoodEntry(moodEntry: MoodEntry)
    
    /**
     * Delete a mood entry
     */
    suspend fun deleteMoodEntry(moodEntry: MoodEntry)
    
    /**
     * Delete a mood entry by ID
     */
    suspend fun deleteMoodEntryById(entryId: String)
    
    /**
     * Delete all mood entries
     */
    suspend fun deleteAllMoodEntries()
    
    /**
     * Delete mood entries older than a specific timestamp
     */
    suspend fun deleteMoodEntriesBefore(beforeTime: Long)
}
