package com.steadymate.app.domain.repository

import com.steadymate.app.domain.model.ReframeEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * Repository interface for cognitive reframing operations.
 * Simplified CBT-lite tool: Situation → Thought → Evidence For/Against → Balanced thought.
 */
interface ReframeRepository {
    
    /**
     * Get all reframe entries as a Flow (reactive stream)
     */
    fun getAllReframeEntriesFlow(): Flow<List<ReframeEntry>>
    
    /**
     * Get all reframe entries
     */
    suspend fun getAllReframeEntries(): List<ReframeEntry>
    
    /**
     * Get a specific reframe entry by ID
     */
    suspend fun getReframeEntryById(entryId: String): ReframeEntry?
    
    /**
     * Get reframe entries within a timestamp range
     */
    suspend fun getReframeEntriesInRange(startTime: Long, endTime: Long): List<ReframeEntry>
    
    /**
     * Get reframe entries within a timestamp range as a Flow
     */
    fun getReframeEntriesInRangeFlow(startTime: Long, endTime: Long): Flow<List<ReframeEntry>>
    
    /**
     * Get recent reframe entries (limited number)
     */
    suspend fun getRecentReframeEntries(limit: Int): List<ReframeEntry>
    
    /**
     * Get total count of reframe entries
     */
    suspend fun getReframeEntryCount(): Int
    
    /**
     * Insert a new reframe entry
     */
    suspend fun insertReframeEntry(reframeEntry: ReframeEntry)
    
    /**
     * Insert multiple reframe entries
     */
    suspend fun insertReframeEntries(reframeEntries: List<ReframeEntry>)
    
    /**
     * Update an existing reframe entry
     */
    suspend fun updateReframeEntry(reframeEntry: ReframeEntry)
    
    /**
     * Delete a reframe entry
     */
    suspend fun deleteReframeEntry(reframeEntry: ReframeEntry)
    
    /**
     * Delete a reframe entry by ID
     */
    suspend fun deleteReframeEntryById(entryId: String)
    
    /**
     * Delete all reframe entries
     */
    suspend fun deleteAllReframeEntries()
    
    /**
     * Delete reframe entries older than a specific timestamp
     */
    suspend fun deleteReframeEntriesBefore(beforeTime: Long)
}
