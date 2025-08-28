package com.steadymate.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.steadymate.app.data.database.entities.MicroWinEntity
import com.steadymate.app.domain.model.WinCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface MicroWinDao {
    
    @Query("SELECT * FROM micro_wins ORDER BY timestamp DESC")
    fun getAllMicroWins(): Flow<List<MicroWinEntity>>
    
    @Query("SELECT * FROM micro_wins WHERE id = :id")
    suspend fun getMicroWinById(id: String): MicroWinEntity?
    
    @Query("SELECT * FROM micro_wins ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMicroWins(limit: Int): List<MicroWinEntity>
    
    @Query("SELECT * FROM micro_wins WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    fun getFilteredMicroWins(startDate: String): Flow<List<MicroWinEntity>>
    
    @Query("SELECT * FROM micro_wins WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getMicroWinsInDateRange(startDate: String, endDate: String): List<MicroWinEntity>
    
    @Query("SELECT * FROM micro_wins WHERE category = :category ORDER BY timestamp DESC")
    fun getMicroWinsByCategory(category: WinCategory): Flow<List<MicroWinEntity>>
    
    @Query("SELECT COUNT(*) FROM micro_wins WHERE DATE(timestamp) = DATE(:date)")
    suspend fun getMicroWinCountForDate(date: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicroWin(microWin: MicroWinEntity): Long
    
    @Update
    suspend fun updateMicroWin(microWin: MicroWinEntity)
    
    @Delete
    suspend fun deleteMicroWin(microWin: MicroWinEntity)
    
    @Query("DELETE FROM micro_wins WHERE id = :id")
    suspend fun deleteMicroWinById(id: String)
    
    @Query("DELETE FROM micro_wins")
    suspend fun deleteAllMicroWins()
}
