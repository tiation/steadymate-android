package com.steadymate.app.data.repository

import com.steadymate.app.data.database.dao.MoodEntryDao
import com.steadymate.app.data.database.entities.toDomainModel
import com.steadymate.app.data.database.entities.toEntity
import com.steadymate.app.di.DispatcherIO
import com.steadymate.app.domain.model.MoodEntry
import com.steadymate.app.domain.repository.MoodRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MoodRepository interface.
 * Simplified for local-first approach - single user per device.
 */
@Singleton
class MoodRepositoryImpl @Inject constructor(
    private val moodEntryDao: MoodEntryDao,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : MoodRepository {

    override fun getAllMoodEntriesFlow(): Flow<List<MoodEntry>> {
        return moodEntryDao.getAllMoodEntriesFlow()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getAllMoodEntries(): List<MoodEntry> {
        return withContext(ioDispatcher) {
            moodEntryDao.getAllMoodEntries()
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getMoodEntryById(entryId: String): MoodEntry? {
        return withContext(ioDispatcher) {
            moodEntryDao.getMoodEntryById(entryId)?.toDomainModel()
        }
    }

    override suspend fun getMoodEntriesInRange(startTime: Long, endTime: Long): List<MoodEntry> {
        return withContext(ioDispatcher) {
            moodEntryDao.getMoodEntriesInRange(startTime, endTime)
                .map { it.toDomainModel() }
        }
    }

    override fun getMoodEntriesInRangeFlow(startTime: Long, endTime: Long): Flow<List<MoodEntry>> {
        return moodEntryDao.getMoodEntriesInRangeFlow(startTime, endTime)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getRecentMoodEntries(limit: Int): List<MoodEntry> {
        return withContext(ioDispatcher) {
            moodEntryDao.getRecentMoodEntries(limit)
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getAverageMoodSince(startTime: Long): Double? {
        return withContext(ioDispatcher) {
            moodEntryDao.getAverageMoodSince(startTime)
        }
    }

    override suspend fun getMoodEntriesByScoreRange(minScore: Int, maxScore: Int): List<MoodEntry> {
        return withContext(ioDispatcher) {
            moodEntryDao.getMoodEntriesByScoreRange(minScore, maxScore)
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getMoodEntryCount(): Int {
        return withContext(ioDispatcher) {
            moodEntryDao.getMoodEntryCount()
        }
    }

    override suspend fun insertMoodEntry(moodEntry: MoodEntry) {
        withContext(ioDispatcher) {
            moodEntryDao.insertMoodEntry(moodEntry.toEntity())
        }
    }

    override suspend fun insertMoodEntries(moodEntries: List<MoodEntry>) {
        withContext(ioDispatcher) {
            moodEntryDao.insertMoodEntries(moodEntries.map { it.toEntity() })
        }
    }

    override suspend fun updateMoodEntry(moodEntry: MoodEntry) {
        withContext(ioDispatcher) {
            moodEntryDao.updateMoodEntry(moodEntry.toEntity())
        }
    }

    override suspend fun deleteMoodEntry(moodEntry: MoodEntry) {
        withContext(ioDispatcher) {
            moodEntryDao.deleteMoodEntry(moodEntry.toEntity())
        }
    }

    override suspend fun deleteMoodEntryById(entryId: String) {
        withContext(ioDispatcher) {
            moodEntryDao.deleteMoodEntryById(entryId)
        }
    }

    override suspend fun deleteAllMoodEntries() {
        withContext(ioDispatcher) {
            moodEntryDao.deleteAllMoodEntries()
        }
    }

    override suspend fun deleteMoodEntriesBefore(beforeTime: Long) {
        withContext(ioDispatcher) {
            moodEntryDao.deleteMoodEntriesBefore(beforeTime)
        }
    }
}
