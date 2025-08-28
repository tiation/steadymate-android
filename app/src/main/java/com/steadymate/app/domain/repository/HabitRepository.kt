package com.steadymate.app.domain.repository

import com.steadymate.app.domain.model.Habit
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for habit tracking operations.
 * Simplified for local-first approach with binary habit tracking.
 */
interface HabitRepository {
    
    /**
     * Get enabled habits as a Flow (reactive stream)
     */
    fun getEnabledHabitsFlow(): Flow<List<Habit>>
    
    /**
     * Get enabled habits
     */
    suspend fun getEnabledHabits(): List<Habit>
    
    /**
     * Get all habits (including disabled)
     */
    suspend fun getAllHabits(): List<Habit>
    
    /**
     * Get a specific habit by ID
     */
    suspend fun getHabitById(habitId: String): Habit?
    
    /**
     * Get a specific habit by ID as a Flow
     */
    fun getHabitByIdFlow(habitId: String): Flow<Habit?>
    
    /**
     * Get habits that have reminders set
     */
    suspend fun getHabitsWithReminders(): List<Habit>
    
    /**
     * Get count of enabled habits
     */
    suspend fun getEnabledHabitCount(): Int
    
    /**
     * Insert a new habit
     */
    suspend fun insertHabit(habit: Habit)
    
    /**
     * Insert multiple habits
     */
    suspend fun insertHabits(habits: List<Habit>)
    
    /**
     * Update an existing habit
     */
    suspend fun updateHabit(habit: Habit)
    
    /**
     * Update habit enabled status
     */
    suspend fun updateHabitEnabledStatus(habitId: String, enabled: Boolean)
    
    /**
     * Update habit details
     */
    suspend fun updateHabitDetails(habitId: String, title: String, reminderTime: String?)
    
    /**
     * Delete a habit
     */
    suspend fun deleteHabit(habit: Habit)
    
    /**
     * Delete a habit by ID
     */
    suspend fun deleteHabitById(habitId: String)
    
    /**
     * Delete all habits
     */
    suspend fun deleteAllHabits()
}
