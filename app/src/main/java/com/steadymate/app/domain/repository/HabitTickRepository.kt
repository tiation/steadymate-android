package com.steadymate.app.domain.repository

import com.steadymate.app.domain.model.HabitTick
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for habit completion tracking operations.
 * Simplified for local-first approach with binary completion tracking.
 */
interface HabitTickRepository {
    
    /**
     * Get all habit ticks as a Flow (reactive stream)
     */
    fun getAllHabitTicksFlow(): Flow<List<HabitTick>>
    
    /**
     * Get all habit ticks
     */
    suspend fun getAllHabitTicks(): List<HabitTick>
    
    /**
     * Get habit ticks for a specific habit
     */
    suspend fun getHabitTicksByHabit(habitId: String): List<HabitTick>
    
    /**
     * Get habit ticks for a specific habit as a Flow
     */
    fun getHabitTicksByHabitFlow(habitId: String): Flow<List<HabitTick>>
    
    /**
     * Get habit tick for a specific habit and date
     */
    suspend fun getHabitTickByDate(habitId: String, date: String): HabitTick?
    
    /**
     * Get habit ticks within a date range
     */
    suspend fun getHabitTicksInDateRange(startDate: String, endDate: String): List<HabitTick>
    
    /**
     * Get habit ticks for a specific habit within a date range
     */
    suspend fun getHabitTicksForHabitInRange(habitId: String, startDate: String, endDate: String): List<HabitTick>
    
    /**
     * Get total completion count for a habit
     */
    suspend fun getHabitCompletionCount(habitId: String): Int
    
    /**
     * Get daily completion count for a specific date
     */
    suspend fun getDailyCompletionCount(date: String): Int
    
    /**
     * Get recent habit ticks (limited number)
     */
    suspend fun getRecentHabitTicks(startDate: String, limit: Int): List<HabitTick>
    
    /**
     * Check if a habit was completed on a specific date
     */
    suspend fun hasTickForDate(habitId: String, date: String): Boolean
    
    /**
     * Insert a habit tick
     */
    suspend fun insertHabitTick(habitTick: HabitTick)
    
    /**
     * Insert multiple habit ticks
     */
    suspend fun insertHabitTicks(habitTicks: List<HabitTick>)
    
    /**
     * Update a habit tick
     */
    suspend fun updateHabitTick(habitTick: HabitTick)
    
    /**
     * Delete a habit tick
     */
    suspend fun deleteHabitTick(habitTick: HabitTick)
    
    /**
     * Delete a specific habit tick by habit and date
     */
    suspend fun deleteHabitTickByHabitAndDate(habitId: String, date: String)
    
    /**
     * Delete all ticks for a specific habit
     */
    suspend fun deleteHabitTicksByHabit(habitId: String)
    
    /**
     * Delete all habit ticks
     */
    suspend fun deleteAllHabitTicks()
    
    /**
     * Delete habit ticks older than a specific date
     */
    suspend fun deleteHabitTicksBefore(beforeDate: String)
}
