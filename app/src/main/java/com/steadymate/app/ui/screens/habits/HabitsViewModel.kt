package com.steadymate.app.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.domain.model.Habit
import com.steadymate.app.domain.model.HabitTick
import com.steadymate.app.domain.repository.HabitRepository
import com.steadymate.app.domain.repository.HabitTickRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for the Habits screen.
 * Manages habit data, completion tracking, and statistics.
 */
@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val habitTickRepository: HabitTickRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitsUiState())
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                // Combine habits with their completion status for today
                combine(
                    habitRepository.getEnabledHabitsFlow(),
                    habitTickRepository.getAllHabitTicksFlow()
                ) { habits, allTicks ->
                    val todayTicks = allTicks.filter { it.date == today }
                    val habitCompletions = habits.map { habit ->
                        val todayTick = todayTicks.find { it.habitId == habit.id }
                        HabitCompletion(
                            habit = habit,
                            isCompletedToday = todayTick?.done ?: false,
                            completionCount = calculateCompletionCount(habit.id, allTicks),
                            currentStreak = calculateCurrentStreak(habit.id, allTicks),
                            completionRate = calculateCompletionRate(habit.id, allTicks)
                        )
                    }
                    habitCompletions
                }.collect { habitCompletions ->
                    _uiState.value = _uiState.value.copy(
                        habits = habitCompletions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load habits: ${e.message}"
                )
            }
        }
    }

    fun toggleHabitCompletion(habitId: String) {
        viewModelScope.launch {
            try {
                val existingTick = habitTickRepository.getHabitTickByDate(habitId, today)
                
                if (existingTick == null) {
                    // Create new completion
                    habitTickRepository.insertHabitTick(
                        HabitTick(
                            habitId = habitId,
                            date = today,
                            done = true
                        )
                    )
                } else {
                    // Toggle existing completion
                    habitTickRepository.updateHabitTick(
                        existingTick.copy(done = !existingTick.done)
                    )
                }
                
                // The UI will update automatically through the Flow
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update habit: ${e.message}"
                )
            }
        }
    }

    fun createHabit(title: String, reminderTime: String? = null, schedule: String = "MTWTFSS") {
        viewModelScope.launch {
            try {
                val habit = Habit(
                    title = title.trim(),
                    schedule = schedule,
                    reminderTime = reminderTime,
                    enabled = true
                )
                
                habitRepository.insertHabit(habit)
                _uiState.value = _uiState.value.copy(showAddHabitDialog = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to create habit: ${e.message}"
                )
            }
        }
    }

    fun updateHabit(habitId: String, title: String, reminderTime: String?) {
        viewModelScope.launch {
            try {
                habitRepository.updateHabitDetails(habitId, title.trim(), reminderTime)
                _uiState.value = _uiState.value.copy(editingHabit = null)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update habit: ${e.message}"
                )
            }
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            try {
                habitRepository.deleteHabitById(habitId)
                // Also delete all associated ticks
                habitTickRepository.deleteHabitTicksByHabit(habitId)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete habit: ${e.message}"
                )
            }
        }
    }

    fun toggleHabitEnabled(habitId: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                habitRepository.updateHabitEnabledStatus(habitId, enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update habit status: ${e.message}"
                )
            }
        }
    }

    fun showAddHabitDialog() {
        _uiState.value = _uiState.value.copy(showAddHabitDialog = true)
    }

    fun hideAddHabitDialog() {
        _uiState.value = _uiState.value.copy(showAddHabitDialog = false)
    }

    fun startEditingHabit(habit: Habit) {
        _uiState.value = _uiState.value.copy(editingHabit = habit)
    }

    fun cancelEditingHabit() {
        _uiState.value = _uiState.value.copy(editingHabit = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun calculateCompletionCount(habitId: String, allTicks: List<HabitTick>): Int {
        return allTicks.count { it.habitId == habitId && it.done }
    }

    private fun calculateCurrentStreak(habitId: String, allTicks: List<HabitTick>): Int {
        val habitTicks = allTicks
            .filter { it.habitId == habitId && it.done }
            .map { LocalDate.parse(it.date) }
            .sortedDescending()

        if (habitTicks.isEmpty()) return 0

        var streak = 0
        var currentDate = LocalDate.now()
        
        // Check if completed today or yesterday (allow for some flexibility)
        val mostRecentCompletion = habitTicks.first()
        if (mostRecentCompletion.isBefore(currentDate.minusDays(1))) {
            return 0 // Streak is broken
        }

        // Count consecutive days
        for (tickDate in habitTicks) {
            if (tickDate == currentDate || tickDate == currentDate.minusDays(1)) {
                streak++
                currentDate = tickDate.minusDays(1)
            } else if (tickDate == currentDate) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    private fun calculateCompletionRate(habitId: String, allTicks: List<HabitTick>): Float {
        val habitTicks = allTicks.filter { it.habitId == habitId }
        if (habitTicks.isEmpty()) return 0f
        
        val completedCount = habitTicks.count { it.done }
        return completedCount.toFloat() / habitTicks.size.toFloat()
    }
}

/**
 * UI state for the Habits screen
 */
data class HabitsUiState(
    val habits: List<HabitCompletion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddHabitDialog: Boolean = false,
    val editingHabit: Habit? = null
)

/**
 * Represents a habit with its completion information
 */
data class HabitCompletion(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val completionCount: Int,
    val currentStreak: Int,
    val completionRate: Float
)
