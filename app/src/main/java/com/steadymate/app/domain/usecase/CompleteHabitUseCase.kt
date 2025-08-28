package com.steadymate.app.domain.usecase

import com.steadymate.app.domain.model.HabitTick
import com.steadymate.app.domain.repository.HabitTickRepository
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher
import com.steadymate.app.di.DispatcherIO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for completing (or toggling) a habit for a specific date.
 * Implements binary habit tracking - done or not done.
 */
@Singleton
class CompleteHabitUseCase @Inject constructor(
    private val habitTickRepository: HabitTickRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    
    /**
     * Toggles the completion state of a habit for today.
     * If the habit is already completed today, it will be uncompleted.
     * If it's not completed, it will be marked as completed.
     * 
     * @param habitId The ID of the habit to toggle
     * @return Result indicating success or failure, with the new completion state
     */
    suspend operator fun invoke(habitId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            val today = getTodayDateString()
            val existingTick = habitTickRepository.getHabitTickByDate(habitId, today)
            
            val newCompletionState = if (existingTick != null) {
                // Toggle existing tick
                val updatedTick = existingTick.copy(done = !existingTick.done)
                habitTickRepository.updateHabitTick(updatedTick)
                updatedTick.done
            } else {
                // Create new tick as completed
                val newTick = HabitTick(
                    habitId = habitId,
                    date = today,
                    done = true
                )
                habitTickRepository.insertHabitTick(newTick)
                true
            }
            
            Result.success(newCompletionState)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Marks a habit as completed for a specific date.
     * 
     * @param habitId The ID of the habit to complete
     * @param date The date to mark as completed (yyyy-MM-dd format)
     * @return Result indicating success or failure
     */
    suspend fun completeForDate(habitId: String, date: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val existingTick = habitTickRepository.getHabitTickByDate(habitId, date)
            
            if (existingTick != null) {
                // Update existing tick to completed
                val updatedTick = existingTick.copy(done = true)
                habitTickRepository.updateHabitTick(updatedTick)
            } else {
                // Create new completed tick
                val newTick = HabitTick(
                    habitId = habitId,
                    date = date,
                    done = true
                )
                habitTickRepository.insertHabitTick(newTick)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Checks if a habit is completed for today.
     * 
     * @param habitId The ID of the habit to check
     * @return true if the habit is completed today, false otherwise
     */
    suspend fun isCompletedToday(habitId: String): Boolean = withContext(ioDispatcher) {
        try {
            val today = getTodayDateString()
            val tick = habitTickRepository.getHabitTickByDate(habitId, today)
            return@withContext tick?.done == true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Gets the completion count for a habit across all time.
     * 
     * @param habitId The ID of the habit
     * @return Total number of completed days for this habit
     */
    suspend fun getCompletionCount(habitId: String): Int = withContext(ioDispatcher) {
        try {
            habitTickRepository.getHabitCompletionCount(habitId)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Calculates the current streak for a habit.
     * 
     * @param habitId The ID of the habit
     * @return Number of consecutive days the habit has been completed (starting from today backwards)
     */
    suspend fun getCurrentStreak(habitId: String): Int = withContext(ioDispatcher) {
        try {
            var streak = 0
            var currentDate = LocalDate.now()
            
            // Go backwards from today, counting consecutive completed days
            while (true) {
                val dateString = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val tick = habitTickRepository.getHabitTickByDate(habitId, dateString)
                
                if (tick?.done == true) {
                    streak++
                    currentDate = currentDate.minusDays(1)
                } else {
                    break
                }
            }
            
            return@withContext streak
        } catch (e: Exception) {
            0
        }
    }
    
    private fun getTodayDateString(): String {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}
