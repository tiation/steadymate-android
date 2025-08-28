package com.steadymate.app.data.repository

import com.steadymate.app.data.database.dao.HabitTickDao
import com.steadymate.app.data.database.entities.toDomainModel
import com.steadymate.app.data.database.entities.toEntity
import com.steadymate.app.di.DispatcherIO
import com.steadymate.app.domain.model.HabitTick
import com.steadymate.app.domain.repository.HabitTickRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of HabitTickRepository interface.
 * Simplified for local-first approach with binary completion tracking.
 */
@Singleton
class HabitTickRepositoryImpl @Inject constructor(
    private val habitTickDao: HabitTickDao,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : HabitTickRepository {

    override fun getAllHabitTicksFlow(): Flow<List<HabitTick>> {
        return habitTickDao.getAllHabitTicksFlow()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getAllHabitTicks(): List<HabitTick> {
        return withContext(ioDispatcher) {
            habitTickDao.getAllHabitTicks()
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getHabitTicksByHabit(habitId: String): List<HabitTick> {
        return withContext(ioDispatcher) {
            habitTickDao.getHabitTicksByHabit(habitId)
                .map { it.toDomainModel() }
        }
    }

    override fun getHabitTicksByHabitFlow(habitId: String): Flow<List<HabitTick>> {
        return habitTickDao.getHabitTicksByHabitFlow(habitId)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getHabitTickByDate(habitId: String, date: String): HabitTick? {
        return withContext(ioDispatcher) {
            habitTickDao.getHabitTickByDate(habitId, date)?.toDomainModel()
        }
    }

    override suspend fun getHabitTicksInDateRange(startDate: String, endDate: String): List<HabitTick> {
        return withContext(ioDispatcher) {
            habitTickDao.getHabitTicksInDateRange(startDate, endDate)
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getHabitTicksForHabitInRange(habitId: String, startDate: String, endDate: String): List<HabitTick> {
        return withContext(ioDispatcher) {
            habitTickDao.getHabitTicksForHabitInRange(habitId, startDate, endDate)
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getHabitCompletionCount(habitId: String): Int {
        return withContext(ioDispatcher) {
            habitTickDao.getHabitCompletionCount(habitId)
        }
    }

    override suspend fun getDailyCompletionCount(date: String): Int {
        return withContext(ioDispatcher) {
            habitTickDao.getDailyCompletionCount(date)
        }
    }

    override suspend fun getRecentHabitTicks(startDate: String, limit: Int): List<HabitTick> {
        return withContext(ioDispatcher) {
            habitTickDao.getRecentHabitTicks(startDate, limit)
                .map { it.toDomainModel() }
        }
    }

    override suspend fun hasTickForDate(habitId: String, date: String): Boolean {
        return withContext(ioDispatcher) {
            habitTickDao.hasTickForDate(habitId, date)
        }
    }

    override suspend fun insertHabitTick(habitTick: HabitTick) {
        withContext(ioDispatcher) {
            habitTickDao.insertHabitTick(habitTick.toEntity())
        }
    }

    override suspend fun insertHabitTicks(habitTicks: List<HabitTick>) {
        withContext(ioDispatcher) {
            habitTickDao.insertHabitTicks(habitTicks.map { it.toEntity() })
        }
    }

    override suspend fun updateHabitTick(habitTick: HabitTick) {
        withContext(ioDispatcher) {
            habitTickDao.updateHabitTick(habitTick.toEntity())
        }
    }

    override suspend fun deleteHabitTick(habitTick: HabitTick) {
        withContext(ioDispatcher) {
            habitTickDao.deleteHabitTick(habitTick.toEntity())
        }
    }

    override suspend fun deleteHabitTickByHabitAndDate(habitId: String, date: String) {
        withContext(ioDispatcher) {
            habitTickDao.deleteHabitTickByHabitAndDate(habitId, date)
        }
    }

    override suspend fun deleteHabitTicksByHabit(habitId: String) {
        withContext(ioDispatcher) {
            habitTickDao.deleteHabitTicksByHabit(habitId)
        }
    }

    override suspend fun deleteAllHabitTicks() {
        withContext(ioDispatcher) {
            habitTickDao.deleteAllHabitTicks()
        }
    }

    override suspend fun deleteHabitTicksBefore(beforeDate: String) {
        withContext(ioDispatcher) {
            habitTickDao.deleteHabitTicksBefore(beforeDate)
        }
    }
}
