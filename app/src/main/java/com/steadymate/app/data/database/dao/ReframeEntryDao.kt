package com.steadymate.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.steadymate.app.data.database.entities.ReframeEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReframeEntryDao {
    
    @Query("SELECT * FROM reframe_entries ORDER BY timestamp DESC")
    fun getAllReframes(): Flow<List<ReframeEntryEntity>>
    
    @Query("SELECT * FROM reframe_entries WHERE id = :id")
    suspend fun getReframeById(id: String): ReframeEntryEntity?
    
    @Query("SELECT * FROM reframe_entries ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentReframes(limit: Int): List<ReframeEntryEntity>
    
    @Query("SELECT * FROM reframe_entries WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    fun getFilteredReframes(startDate: String): Flow<List<ReframeEntryEntity>>
    
    @Query("SELECT * FROM reframe_entries WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getReframesInDateRange(startDate: String, endDate: String): List<ReframeEntryEntity>
    
    @Query("SELECT COUNT(*) FROM reframe_entries WHERE DATE(timestamp) = DATE(:date)")
    suspend fun getReframeCountForDate(date: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReframe(reframe: ReframeEntryEntity): Long
    
    @Update
    suspend fun updateReframe(reframe: ReframeEntryEntity)
    
    @Delete
    suspend fun deleteReframe(reframe: ReframeEntryEntity)
    
    @Query("DELETE FROM reframe_entries WHERE id = :id")
    suspend fun deleteReframeById(id: String)
    
    @Query("DELETE FROM reframe_entries")
    suspend fun deleteAllReframes()
}
