package com.steadymate.app.data.database.dao

import androidx.room.*
import com.steadymate.app.data.database.entities.HabitTickEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for HabitTick entity operations.
 * Simplified to match requirements - binary completion tracking.
 */
@Dao
interface HabitTickDao {
    
    @Query("SELECT * FROM habit_ticks ORDER BY date DESC")
    fun getAllHabitTicksFlow(): Flow<List<HabitTickEntity>>
    
    @Query("SELECT * FROM habit_ticks ORDER BY date DESC")
    suspend fun getAllHabitTicks(): List<HabitTickEntity>
    
    @Query("SELECT * FROM habit_ticks WHERE habitId = :habitId ORDER BY date DESC")
    suspend fun getHabitTicksByHabit(habitId: String): List<HabitTickEntity>
    
    @Query("SELECT * FROM habit_ticks WHERE habitId = :habitId ORDER BY date DESC")
    fun getHabitTicksByHabitFlow(habitId: String): Flow<List<HabitTickEntity>>
    
    @Query("SELECT * FROM habit_ticks WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getHabitTickByDate(habitId: String, date: String): HabitTickEntity?
    
    @Query("SELECT * FROM habit_ticks WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getHabitTicksInDateRange(startDate: String, endDate: String): List<HabitTickEntity>
    
    @Query("SELECT * FROM habit_ticks WHERE habitId = :habitId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getHabitTicksForHabitInRange(habitId: String, startDate: String, endDate: String): List<HabitTickEntity>
    
    @Query("SELECT COUNT(*) FROM habit_ticks WHERE habitId = :habitId AND done = 1")
    suspend fun getHabitCompletionCount(habitId: String): Int
    
    @Query("SELECT COUNT(*) FROM habit_ticks WHERE date = :date AND done = 1")
    suspend fun getDailyCompletionCount(date: String): Int
    
    @Query("SELECT * FROM habit_ticks WHERE date >= :startDate ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentHabitTicks(startDate: String, limit: Int): List<HabitTickEntity>
    
    @Query("SELECT EXISTS(SELECT 1 FROM habit_ticks WHERE habitId = :habitId AND date = :date)")
    suspend fun hasTickForDate(habitId: String, date: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitTick(habitTick: HabitTickEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitTicks(habitTicks: List<HabitTickEntity>)
    
    @Update
    suspend fun updateHabitTick(habitTick: HabitTickEntity)
    
    @Delete
    suspend fun deleteHabitTick(habitTick: HabitTickEntity)
    
    @Query("DELETE FROM habit_ticks WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitTickByHabitAndDate(habitId: String, date: String)
    
    @Query("DELETE FROM habit_ticks WHERE habitId = :habitId")
    suspend fun deleteHabitTicksByHabit(habitId: String)
    
    @Query("DELETE FROM habit_ticks")
    suspend fun deleteAllHabitTicks()
    
    @Query("DELETE FROM habit_ticks WHERE date < :beforeDate")
    suspend fun deleteHabitTicksBefore(beforeDate: String)
}
