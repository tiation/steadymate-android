package com.steadymate.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.steadymate.app.data.database.entities.WorryEntryEntity
import com.steadymate.app.domain.model.WorryCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface WorryEntryDao {
    
    @Query("SELECT * FROM worry_entries ORDER BY timestamp DESC")
    fun getAllWorries(): Flow<List<WorryEntryEntity>>
    
    @Query("SELECT * FROM worry_entries WHERE id = :id")
    suspend fun getWorryById(id: String): WorryEntryEntity?
    
    @Query("SELECT * FROM worry_entries ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentWorries(limit: Int): List<WorryEntryEntity>
    
    @Query("SELECT * FROM worry_entries WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    fun getFilteredWorries(startDate: String): Flow<List<WorryEntryEntity>>
    
    @Query("SELECT * FROM worry_entries WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getWorriesInDateRange(startDate: String, endDate: String): List<WorryEntryEntity>
    
    @Query("SELECT * FROM worry_entries WHERE category = :category ORDER BY timestamp DESC")
    fun getWorriesByCategory(category: WorryCategory): Flow<List<WorryEntryEntity>>
    
    @Query("SELECT * FROM worry_entries WHERE isParked = 1 ORDER BY scheduledRevisit ASC")
    fun getParkedWorries(): Flow<List<WorryEntryEntity>>
    
    @Query("SELECT COUNT(*) FROM worry_entries WHERE DATE(timestamp) = DATE(:date)")
    suspend fun getWorryCountForDate(date: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorry(worry: WorryEntryEntity): Long
    
    @Update
    suspend fun updateWorry(worry: WorryEntryEntity)
    
    @Delete
    suspend fun deleteWorry(worry: WorryEntryEntity)
    
    @Query("DELETE FROM worry_entries WHERE id = :id")
    suspend fun deleteWorryById(id: String)
    
    @Query("DELETE FROM worry_entries")
    suspend fun deleteAllWorries()
}
