package com.steadymate.app.ui.screens.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.domain.model.BreathingCategory
import com.steadymate.app.domain.model.BreathingExercise
import com.steadymate.app.domain.model.BreathingPhase
import com.steadymate.app.domain.model.BreathingSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for tools screen with breathing exercises
 */
@HiltViewModel
class ToolsViewModel @Inject constructor(
    // Add any dependencies here (repositories, use cases, etc.)
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolsUiState())
    val uiState: StateFlow<ToolsUiState> = _uiState.asStateFlow()
    
    private val _breathingSession = MutableStateFlow<BreathingSessionState?>(null)
    val breathingSession: StateFlow<BreathingSessionState?> = _breathingSession.asStateFlow()
    
    private var sessionJob: Job? = null
    private val availableExercises = BreathingExercise.PRESETS
    
    init {
        loadInitialState()
    }
    
    private fun loadInitialState() {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            availableExercises = availableExercises,
            exercisesByCategory = groupExercisesByCategory()
        )
    }
    
    private fun groupExercisesByCategory(): Map<BreathingCategory, List<BreathingExercise>> {
        return availableExercises.groupBy { it.category }
    }
    
    /**
     * Start a breathing exercise session
     */
    fun startBreathingExercise(exercise: BreathingExercise) {
        // Stop any existing session
        stopBreathingExercise()
        
        val initialState = BreathingSessionState(
            exercise = exercise,
            isActive = true,
            currentPhase = BreathingPhase.INHALE,
            animationScale = 0.3f
        )
        
        _breathingSession.value = initialState
        startSessionTimer(initialState)
    }
    
    /**
     * Pause the current breathing session
     */
    fun pauseBreathingExercise() {
        _breathingSession.value?.let { currentState ->
            _breathingSession.value = currentState.copy(
                isPaused = true,
                isActive = false
            )
            sessionJob?.cancel()
        }
    }
    
    /**
     * Resume the paused breathing session
     */
    fun resumeBreathingExercise() {
        _breathingSession.value?.let { currentState ->
            if (currentState.isPaused) {
                val resumedState = currentState.copy(
                    isPaused = false,
                    isActive = true
                )
                _breathingSession.value = resumedState
                startSessionTimer(resumedState)
            }
        }
    }
    
    /**
     * Stop the current breathing session
     */
    fun stopBreathingExercise() {
        sessionJob?.cancel()
        sessionJob = null
        _breathingSession.value = null
    }
    
    /**
     * Filter exercises by category
     */
    fun filterByCategory(category: BreathingCategory?) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category
        )
    }
    
    /**
     * Clear any error messages
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    private fun startSessionTimer(initialState: BreathingSessionState) {
        sessionJob = viewModelScope.launch {
            var currentState = initialState
            
            while (currentState.isActive && !currentState.isComplete) {
                delay(1000) // 1 second intervals
                
                currentState = updateBreathingState(currentState)
                _breathingSession.value = currentState
                
                if (currentState.isComplete) {
                    break
                }
            }
        }
    }
    
    private fun updateBreathingState(state: BreathingSessionState): BreathingSessionState {
        val exercise = state.exercise
        var newState = state.copy(
            secondsInCurrentPhase = state.secondsInCurrentPhase + 1,
            totalElapsedSeconds = state.totalElapsedSeconds + 1
        )
        
        // Determine phase durations
        val phaseDuration = when (state.currentPhase) {
            BreathingPhase.INHALE -> exercise.inhaleSeconds
            BreathingPhase.HOLD_INHALE -> exercise.holdInhaleSeconds
            BreathingPhase.EXHALE -> exercise.exhaleSeconds
            BreathingPhase.HOLD_EXHALE -> exercise.holdExhaleSeconds
            BreathingPhase.COMPLETE -> 0
        }
        
        // Update animation scale based on phase
        newState = newState.copy(
            animationScale = calculateAnimationScale(newState.currentPhase, newState.secondsInCurrentPhase, phaseDuration)
        )
        
        // Check if we need to advance to next phase
        if (newState.secondsInCurrentPhase >= phaseDuration) {
            newState = advanceToNextPhase(newState)
        }
        
        return newState
    }
    
    private fun calculateAnimationScale(phase: BreathingPhase, seconds: Int, totalDuration: Int): Float {
        if (totalDuration == 0) return 0.5f
        
        val progress = seconds.toFloat() / totalDuration.toFloat()
        
        return when (phase) {
            BreathingPhase.INHALE -> 0.3f + (progress * 0.4f) // Scale from 0.3 to 0.7
            BreathingPhase.HOLD_INHALE -> 0.7f // Stay at max
            BreathingPhase.EXHALE -> 0.7f - (progress * 0.4f) // Scale from 0.7 to 0.3
            BreathingPhase.HOLD_EXHALE -> 0.3f // Stay at min
            BreathingPhase.COMPLETE -> 0.5f
        }
    }
    
    private fun advanceToNextPhase(state: BreathingSessionState): BreathingSessionState {
        val exercise = state.exercise
        
        val (nextPhase, newCycle) = when (state.currentPhase) {
            BreathingPhase.INHALE -> {
                if (exercise.holdInhaleSeconds > 0) {
                    BreathingPhase.HOLD_INHALE to state.currentCycle
                } else {
                    BreathingPhase.EXHALE to state.currentCycle
                }
            }
            BreathingPhase.HOLD_INHALE -> BreathingPhase.EXHALE to state.currentCycle
            BreathingPhase.EXHALE -> {
                if (exercise.holdExhaleSeconds > 0) {
                    BreathingPhase.HOLD_EXHALE to state.currentCycle
                } else {
                    // Complete cycle, check if we're done
                    val nextCycle = state.currentCycle + 1
                    if (nextCycle >= exercise.totalCycles) {
                        BreathingPhase.COMPLETE to nextCycle
                    } else {
                        BreathingPhase.INHALE to nextCycle
                    }
                }
            }
            BreathingPhase.HOLD_EXHALE -> {
                val nextCycle = state.currentCycle + 1
                if (nextCycle >= exercise.totalCycles) {
                    BreathingPhase.COMPLETE to nextCycle
                } else {
                    BreathingPhase.INHALE to nextCycle
                }
            }
            BreathingPhase.COMPLETE -> BreathingPhase.COMPLETE to state.currentCycle
        }
        
        return state.copy(
            currentPhase = nextPhase,
            currentCycle = newCycle,
            secondsInCurrentPhase = 0,
            isActive = nextPhase != BreathingPhase.COMPLETE
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        stopBreathingExercise()
    }
}

/**
 * UI state for tools screen
 */
data class ToolsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableExercises: List<BreathingExercise> = emptyList(),
    val exercisesByCategory: Map<BreathingCategory, List<BreathingExercise>> = emptyMap(),
    val selectedCategory: BreathingCategory? = null
)
