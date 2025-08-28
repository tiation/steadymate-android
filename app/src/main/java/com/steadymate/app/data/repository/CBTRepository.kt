package com.steadymate.app.data.repository

import com.steadymate.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface CBTRepository {
    // Reframe entries
    suspend fun insertReframeEntry(entry: ReframeEntry)
    suspend fun getReframeEntries(limit: Int = 50): List<ReframeEntry>
    suspend fun getReframeEntriesFlow(): Flow<List<ReframeEntry>>
    suspend fun getReframeEntry(id: String): ReframeEntry?
    suspend fun deleteReframeEntry(id: String)
    
    // Worry entries
    suspend fun insertWorryEntry(entry: WorryEntry)
    suspend fun getWorryEntries(limit: Int = 50): List<WorryEntry>
    suspend fun getActiveWorries(): List<WorryEntry>
    suspend fun getParkedWorries(): List<WorryEntry>
    suspend fun updateWorryEntry(entry: WorryEntry)
    suspend fun deleteWorryEntry(id: String)
    
    // Micro wins
    suspend fun insertMicroWin(win: MicroWin)
    suspend fun getMicroWins(limit: Int = 50): List<MicroWin>
    suspend fun getMicroWinsFlow(): Flow<List<MicroWin>>
    suspend fun deleteMicroWin(id: String)
    
    // Analytics and insights
    suspend fun getCBTInsights(days: Int = 30): CBTInsights
    suspend fun getReframeStats(days: Int = 7): ReframeStats
    suspend fun getWorryStats(days: Int = 7): WorryStats
}

data class ReframeStats(
    val totalReframes: Int,
    val averageImprovement: Float,
    val streakDays: Int,
    val mostCommonTags: List<String>
)

data class WorryStats(
    val totalWorries: Int,
    val totalWorryTime: Int, // minutes
    val averageWorryTime: Float,
    val resolvedPercentage: Float,
    val commonCategories: List<WorryCategory>
)

/**
 * Simplified in-memory implementation of CBTRepository for MVP.
 * TODO: Replace with proper database persistence when CBT entities are implemented.
 */
@Singleton
class CBTRepositoryImpl @Inject constructor() : CBTRepository {

    private val reframeEntries = mutableListOf<ReframeEntry>()
    private val worryEntries = mutableListOf<WorryEntry>()
    private val microWins = mutableListOf<MicroWin>()

    override suspend fun insertReframeEntry(entry: ReframeEntry) {
        reframeEntries.add(entry)
    }

    override suspend fun getReframeEntries(limit: Int): List<ReframeEntry> {
        return reframeEntries.takeLast(limit).reversed()
    }

    override suspend fun getReframeEntriesFlow(): Flow<List<ReframeEntry>> {
        return flowOf(reframeEntries.reversed())
    }

    override suspend fun getReframeEntry(id: String): ReframeEntry? {
        return reframeEntries.find { it.id == id }
    }

    override suspend fun deleteReframeEntry(id: String) {
        reframeEntries.removeAll { it.id == id }
    }

    override suspend fun insertWorryEntry(entry: WorryEntry) {
        worryEntries.add(entry)
    }

    override suspend fun getWorryEntries(limit: Int): List<WorryEntry> {
        return worryEntries.takeLast(limit).reversed()
    }

    override suspend fun getActiveWorries(): List<WorryEntry> {
        return worryEntries.filter { !it.isResolved }
    }

    override suspend fun getParkedWorries(): List<WorryEntry> {
        return worryEntries.filter { it.isParked }
    }

    override suspend fun updateWorryEntry(entry: WorryEntry) {
        val index = worryEntries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            worryEntries[index] = entry
        }
    }

    override suspend fun deleteWorryEntry(id: String) {
        worryEntries.removeAll { it.id == id }
    }

    override suspend fun insertMicroWin(win: MicroWin) {
        microWins.add(win)
    }

    override suspend fun getMicroWins(limit: Int): List<MicroWin> {
        return microWins.takeLast(limit).reversed()
    }

    override suspend fun getMicroWinsFlow(): Flow<List<MicroWin>> {
        return flowOf(microWins.reversed())
    }

    override suspend fun deleteMicroWin(id: String) {
        microWins.removeAll { it.id == id }
    }

    override suspend fun getCBTInsights(days: Int): CBTInsights {
        val cutoffDate = LocalDateTime.now().minusDays(days.toLong())
        
        val reframes = getFilteredReframes(cutoffDate)
        val worries = getFilteredWorries(cutoffDate)
        val wins = getFilteredMicroWins(cutoffDate)
        
        val averageImprovement = if (reframes.isNotEmpty()) {
            reframes.map { it.getImprovementScore() }.average().toFloat()
        } else 0f
        
        val totalWorryTimeMinutes = worries.sumOf { it.worryTimeSeconds } / 60
        val worriesResolved = worries.count { it.isResolved }
        
        return CBTInsights(
            totalReframes = reframes.size,
            averageImprovementScore = averageImprovement,
            commonThoughtPatterns = extractCommonPatterns(reframes),
            totalWorryTime = totalWorryTimeMinutes,
            worriesResolved = worriesResolved,
            microWinsCount = wins.size,
            streakDays = 0 // Simplified for MVP
        )
    }

    override suspend fun getReframeStats(days: Int): ReframeStats {
        val cutoffDate = LocalDateTime.now().minusDays(days.toLong())
        val reframes = getFilteredReframes(cutoffDate)
        
        val averageImprovement = if (reframes.isNotEmpty()) {
            reframes.map { it.getImprovementScore() }.average().toFloat()
        } else 0f
        
        val allTags = reframes.flatMap { it.tags }
        val mostCommonTags = allTags.groupingBy { it }.eachCount()
            .toList().sortedByDescending { it.second }
            .take(5).map { it.first }
        
        return ReframeStats(
            totalReframes = reframes.size,
            averageImprovement = averageImprovement,
            streakDays = 0, // Simplified for MVP
            mostCommonTags = mostCommonTags
        )
    }

    override suspend fun getWorryStats(days: Int): WorryStats {
        val cutoffDate = LocalDateTime.now().minusDays(days.toLong())
        val worries = getFilteredWorries(cutoffDate)
        
        val totalWorryTimeMinutes = worries.sumOf { it.worryTimeSeconds } / 60
        val averageWorryTime = if (worries.isNotEmpty()) {
            worries.map { it.worryTimeSeconds }.average().toFloat() / 60f
        } else 0f
        
        val resolvedCount = worries.count { it.isResolved }
        val resolvedPercentage = if (worries.isNotEmpty()) {
            (resolvedCount.toFloat() / worries.size) * 100f
        } else 0f
        
        val categoryFrequency = worries.groupingBy { it.category }.eachCount()
        val commonCategories = categoryFrequency.toList()
            .sortedByDescending { it.second }
            .take(3).map { it.first }
        
        return WorryStats(
            totalWorries = worries.size,
            totalWorryTime = totalWorryTimeMinutes,
            averageWorryTime = averageWorryTime,
            resolvedPercentage = resolvedPercentage,
            commonCategories = commonCategories
        )
    }

    private fun getFilteredReframes(cutoffDate: LocalDateTime): List<ReframeEntry> {
        return reframeEntries.filter { it.getDateTime().isAfter(cutoffDate) }
    }
    
    private fun getFilteredWorries(cutoffDate: LocalDateTime): List<WorryEntry> {
        return worryEntries.filter { it.getDateTime().isAfter(cutoffDate) }
    }
    
    private fun getFilteredMicroWins(cutoffDate: LocalDateTime): List<MicroWin> {
        return microWins.filter { it.getDateTime().isAfter(cutoffDate) }
    }

    private fun extractCommonPatterns(reframes: List<ReframeEntry>): List<String> {
        // Simple pattern extraction based on common words in automatic thoughts
        val allThoughts = reframes.map { it.automaticThought.lowercase() }
        val commonWords = allThoughts.flatMap { it.split(" ") }
            .filter { it.length > 3 } // Filter out short words
            .groupingBy { it }.eachCount()
            .filter { it.value > 1 } // Only words that appear more than once
            .toList().sortedByDescending { it.second }
            .take(5).map { it.first }
        
        return commonWords
    }

    private fun calculateStreak(activeDays: List<java.time.LocalDate>): Int {
        if (activeDays.isEmpty()) return 0
        
        val today = java.time.LocalDate.now()
        var streak = 0
        var currentDate = today
        
        // Work backwards from today
        while (activeDays.contains(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }
        
        return streak
    }
}
