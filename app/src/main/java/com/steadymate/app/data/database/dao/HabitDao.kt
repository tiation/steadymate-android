package com.steadymate.app.data.database.dao

import androidx.room.*
import com.steadymate.app.data.database.entities.HabitEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Habit entity operations.
 * Simplified to match requirements - small, binary habits with optional reminders.
 */
@Dao
interface HabitDao {
    
    @Query("SELECT * FROM habits WHERE enabled = 1 ORDER BY title ASC")
    fun getEnabledHabitsFlow(): Flow<List<HabitEntity>>
    
    @Query("SELECT * FROM habits WHERE enabled = 1 ORDER BY title ASC")
    suspend fun getEnabledHabits(): List<HabitEntity>
    
    @Query("SELECT * FROM habits ORDER BY title ASC")
    suspend fun getAllHabits(): List<HabitEntity>
    
    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: String): HabitEntity?
    
    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitByIdFlow(habitId: String): Flow<HabitEntity?>
    
    @Query("SELECT * FROM habits WHERE reminderTime IS NOT NULL AND enabled = 1")
    suspend fun getHabitsWithReminders(): List<HabitEntity>
    
    @Query("SELECT COUNT(*) FROM habits WHERE enabled = 1")
    suspend fun getEnabledHabitCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)
    
    @Update
    suspend fun updateHabit(habit: HabitEntity)
    
    @Query("UPDATE habits SET enabled = :enabled WHERE id = :habitId")
    suspend fun updateHabitEnabledStatus(habitId: String, enabled: Boolean)
    
    @Query("UPDATE habits SET title = :title, reminderTime = :reminderTime WHERE id = :habitId")
    suspend fun updateHabitDetails(habitId: String, title: String, reminderTime: String?)
    
    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
    
    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: String)
    
    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}
