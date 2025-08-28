package com.steadymate.app.ui.screens.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.domain.model.MoodEntry
import com.steadymate.app.domain.repository.MoodRepository
import com.steadymate.app.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    private val _chartData = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val chartData: StateFlow<List<ChartDataPoint>> = _chartData.asStateFlow()

    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    init {
        viewModelScope.launch {
            loadInitialData()
        }
    }
    
    private suspend fun loadInitialData() {
        val recentEntries = moodRepository.getRecentMoodEntries(30)
        _uiState.value = _uiState.value.copy(
            currentStreak = calculateStreak(recentEntries),
            hasCheckedInToday = hasCheckedInToday(recentEntries)
        )
        updateChartData(recentEntries)
    }

    fun updateMoodLevel(level: Int) {
        _uiState.value = _uiState.value.copy(
            moodLevel = level,
            isValidInput = level in 0..10 && _uiState.value.selectedTags.isNotEmpty()
        )
    }

    fun toggleTag(tag: String) {
        val currentTags = _uiState.value.selectedTags.toMutableList()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }
        
        _uiState.value = _uiState.value.copy(
            selectedTags = currentTags,
            isValidInput = _uiState.value.moodLevel > 0 && currentTags.isNotEmpty()
        )
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun submitCheckIn() {
        if (!_uiState.value.isValidInput) return

        _uiState.value = _uiState.value.copy(isSubmitting = true)

        viewModelScope.launch {
            try {
                val moodEntry = MoodEntry(
                    userId = "user_1", // TODO: Get actual user ID
                    moodLevel = _uiState.value.moodLevel,
                    emotionTags = _uiState.value.selectedTags,
                    notes = _uiState.value.notes,
                    timestamp = now
                )

                moodRepository.insertMoodEntry(moodEntry)

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    isSubmitted = true,
                    currentStreak = _uiState.value.currentStreak + 1,
                    hasCheckedInToday = true
                )

                // Refresh chart data
                val recentEntries = moodRepository.getRecentMoodEntries(30)
                updateChartData(recentEntries)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = "Failed to save check-in: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun updateChartPeriod(period: ChartPeriod) {
        _uiState.value = _uiState.value.copy(selectedChartPeriod = period)
        
        viewModelScope.launch {
            val days = when (period) {
                ChartPeriod.WEEK -> 7
                ChartPeriod.MONTH -> 30
                ChartPeriod.THREE_MONTHS -> 90
            }
            
            val entries = moodRepository.getRecentMoodEntries(days)
            updateChartData(entries)
        }
    }

    private fun calculateStreak(entries: List<MoodEntry>): Int {
        if (entries.isEmpty()) return 0

        // Sort entries by date (most recent first)
        val sortedEntries = entries.sortedByDescending { it.timestamp.date }
        var streak = 0
        var currentDate = now.date

        for (entry in sortedEntries) {
            val entryDate = entry.timestamp.date
            
            if (entryDate == currentDate) {
                streak++
                currentDate = currentDate.minus(DatePeriod(days = 1))
            } else if (entryDate == currentDate.minus(DatePeriod(days = 1))) {
                streak++
                currentDate = entryDate.minus(DatePeriod(days = 1))
            } else {
                break
            }
        }

        return streak
    }

    private fun hasCheckedInToday(entries: List<MoodEntry>): Boolean {
        val today = now.date
        return entries.any { it.timestamp.date == today }
    }

    private fun updateChartData(entries: List<MoodEntry>) {
        val chartPoints = entries.map { entry ->
            ChartDataPoint(
                date = entry.timestamp.date,
                moodLevel = entry.moodLevel,
                timestamp = entry.timestamp
            )
        }.sortedBy { it.date }

        _chartData.value = chartPoints
    }
}

data class CheckInUiState(
    val moodLevel: Int = 0,
    val selectedTags: List<String> = emptyList(),
    val notes: String = "",
    val isValidInput: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null,
    val currentStreak: Int = 0,
    val hasCheckedInToday: Boolean = false,
    val selectedChartPeriod: ChartPeriod = ChartPeriod.WEEK,
    val availableTags: List<String> = listOf(
        "Happy", "Sad", "Anxious", "Calm", "Excited", 
        "Tired", "Energetic", "Stressed", "Relaxed", 
        "Angry", "Content", "Worried", "Hopeful", 
        "Lonely", "Connected", "Overwhelmed"
    )
)

enum class ChartPeriod {
    WEEK, MONTH, THREE_MONTHS
}

data class ChartDataPoint(
    val date: LocalDate,
    val moodLevel: Int,
    val timestamp: LocalDateTime
)
