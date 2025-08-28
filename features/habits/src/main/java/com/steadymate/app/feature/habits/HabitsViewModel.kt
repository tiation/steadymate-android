package com.steadymate.app.feature.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.data.repository.HabitRepository
import com.steadymate.app.domain.repository.UserRepository
import com.steadymate.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

/**
 * ViewModel for the Habits feature.
 * Manages habit creation, completion tracking, and statistics.
 */
@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitsUiState())
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    private val _showTemplateSheet = MutableStateFlow(false)
    val showTemplateSheet: StateFlow<Boolean> = _showTemplateSheet.asStateFlow()

    private val _showAddHabitDialog = MutableStateFlow(false)
    val showAddHabitDialog: StateFlow<Boolean> = _showAddHabitDialog.asStateFlow()

    private val _selectedHabitForSkip = MutableStateFlow<HabitCompletionState?>(null)
    val selectedHabitForSkip: StateFlow<HabitCompletionState?> = _selectedHabitForSkip.asStateFlow()

    init {
        loadTodayHabits()
        loadWeeklyOverview()
    }

    fun loadTodayHabits() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val todayHabits = habitRepository.getTodayCompletionStates(currentUser.id)
                    
                    _uiState.update { currentState ->
                        currentState.copy(
                            todayHabits = todayHabits,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "No user found"
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )}
            }
        }
    }

    private fun loadWeeklyOverview() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val startOfWeek = getStartOfWeek(today)
                    
                    val weeklyData = habitRepository.getWeeklyOverview(currentUser.id, startOfWeek)
                    
                    _uiState.update { currentState ->
                        currentState.copy(
                            weeklyOverview = weeklyData,
                            currentWeekStart = startOfWeek
                        )
                    }
                }
            } catch (e: Exception) {
                // Silently handle weekly overview errors, don't block main UI
            }
        }
    }

    fun completeHabit(habitCompletionState: HabitCompletionState, notes: String = "") {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val habitId = habitCompletionState.habit.id.toLongOrNull()
                    if (habitId != null) {
                        habitRepository.completeHabit(
                            habitId = habitId,
                            userId = currentUser.id,
                            notes = notes
                        )
                        
                        // Refresh data after completion
                        loadTodayHabits()
                        loadWeeklyOverview()
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to complete habit") }
            }
        }
    }

    fun skipHabit(habitCompletionState: HabitCompletionState, reason: String) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val habitId = habitCompletionState.habit.id.toLongOrNull()
                    if (habitId != null) {
                        habitRepository.skipHabit(
                            habitId = habitId,
                            userId = currentUser.id,
                            reason = reason
                        )
                        
                        // Refresh data after skipping
                        loadTodayHabits()
                        loadWeeklyOverview()
                        _selectedHabitForSkip.value = null
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to skip habit") }
            }
        }
    }

    fun showSkipDialog(habitCompletionState: HabitCompletionState) {
        _selectedHabitForSkip.value = habitCompletionState.copy(showSkipDialog = true)
    }

    fun hideSkipDialog() {
        _selectedHabitForSkip.value = null
    }

    fun createHabitFromTemplate(template: HabitTemplate) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser != null) {
                    habitRepository.createHabitFromTemplate(template, currentUser.id)
                    
                    // Refresh habits after creation
                    loadTodayHabits()
                    loadWeeklyOverview()
                    
                    _showTemplateSheet.value = false
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to create habit") }
            }
        }
    }

    fun showTemplateSheet() {
        _showTemplateSheet.value = true
    }

    fun hideTemplateSheet() {
        _showTemplateSheet.value = false
    }

    fun showAddHabitDialog() {
        _showAddHabitDialog.value = true
    }

    fun hideAddHabitDialog() {
        _showAddHabitDialog.value = false
    }

    fun navigateToWeek(offset: Int) {
        val currentStart = _uiState.value.currentWeekStart
        val newStart = currentStart.plus(offset * 7, DateTimeUnit.DAY)
        
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val weeklyData = habitRepository.getWeeklyOverview(currentUser.id, newStart)
                    
                    _uiState.update { currentState ->
                        currentState.copy(
                            weeklyOverview = weeklyData,
                            currentWeekStart = newStart
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to load week data") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refreshData() {
        loadTodayHabits()
        loadWeeklyOverview()
    }

    private fun getStartOfWeek(date: LocalDate): LocalDate {
        val dayOfWeek = date.dayOfWeek.value // Monday = 1, Sunday = 7
        return date.minus(dayOfWeek - 1, DateTimeUnit.DAY)
    }
}

/**
 * UI State for the Habits screen
 */
data class HabitsUiState(
    val todayHabits: List<HabitCompletionState> = emptyList(),
    val weeklyOverview: Map<LocalDate, List<HabitCompletionState>> = emptyMap(),
    val currentWeekStart: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedCategory: HabitCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val completedTodayCount: Int = todayHabits.count { it.isCompleted }
    val totalTodayCount: Int = todayHabits.size
    val todayCompletionRate: Float = if (totalTodayCount > 0) completedTodayCount.toFloat() / totalTodayCount else 0f
    
    val categorizedHabits: Map<HabitCategory, List<HabitCompletionState>> = 
        todayHabits.groupBy { it.habit.category }
    
    val filteredHabits: List<HabitCompletionState> = 
        selectedCategory?.let { category ->
            todayHabits.filter { it.habit.category == category }
        } ?: todayHabits
}
