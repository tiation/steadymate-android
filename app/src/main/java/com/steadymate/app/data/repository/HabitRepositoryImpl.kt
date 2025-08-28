package com.steadymate.app.data.repository

import com.steadymate.app.data.database.dao.HabitDao
import com.steadymate.app.data.database.entities.toDomainModel
import com.steadymate.app.data.database.entities.toEntity
import com.steadymate.app.di.DispatcherIO
import com.steadymate.app.domain.model.Habit
import com.steadymate.app.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of HabitRepository interface.
 * Simplified for local-first approach with binary habit tracking.
 */
@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : HabitRepository {

    override fun getEnabledHabitsFlow(): Flow<List<Habit>> {
        return habitDao.getEnabledHabitsFlow()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getEnabledHabits(): List<Habit> {
        return withContext(ioDispatcher) {
            habitDao.getEnabledHabits()
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getAllHabits(): List<Habit> {
        return withContext(ioDispatcher) {
            habitDao.getAllHabits()
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getHabitById(habitId: String): Habit? {
        return withContext(ioDispatcher) {
            habitDao.getHabitById(habitId)?.toDomainModel()
        }
    }

    override fun getHabitByIdFlow(habitId: String): Flow<Habit?> {
        return habitDao.getHabitByIdFlow(habitId)
            .map { entity -> entity?.toDomainModel() }
    }

    override suspend fun getHabitsWithReminders(): List<Habit> {
        return withContext(ioDispatcher) {
            habitDao.getHabitsWithReminders()
                .map { it.toDomainModel() }
        }
    }

    override suspend fun getEnabledHabitCount(): Int {
        return withContext(ioDispatcher) {
            habitDao.getEnabledHabitCount()
        }
    }

    override suspend fun insertHabit(habit: Habit) {
        withContext(ioDispatcher) {
            habitDao.insertHabit(habit.toEntity())
        }
    }

    override suspend fun insertHabits(habits: List<Habit>) {
        withContext(ioDispatcher) {
            habitDao.insertHabits(habits.map { it.toEntity() })
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        withContext(ioDispatcher) {
            habitDao.updateHabit(habit.toEntity())
        }
    }

    override suspend fun updateHabitEnabledStatus(habitId: String, enabled: Boolean) {
        withContext(ioDispatcher) {
            habitDao.updateHabitEnabledStatus(habitId, enabled)
        }
    }

    override suspend fun updateHabitDetails(habitId: String, title: String, reminderTime: String?) {
        withContext(ioDispatcher) {
            habitDao.updateHabitDetails(habitId, title, reminderTime)
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        withContext(ioDispatcher) {
            habitDao.deleteHabit(habit.toEntity())
        }
    }

    override suspend fun deleteHabitById(habitId: String) {
        withContext(ioDispatcher) {
            habitDao.deleteHabitById(habitId)
        }
    }

    override suspend fun deleteAllHabits() {
        withContext(ioDispatcher) {
            habitDao.deleteAllHabits()
        }
    }
}
