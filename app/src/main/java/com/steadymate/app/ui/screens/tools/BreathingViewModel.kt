package com.steadymate.app.ui.screens.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.domain.model.BreathingSession
import com.steadymate.app.domain.model.BreathingPhase
import com.steadymate.app.domain.repository.BreathingSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@HiltViewModel
class BreathingViewModel @Inject constructor(
    private val breathingSessionRepository: BreathingSessionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BreathingUiState())
    val uiState: StateFlow<BreathingUiState> = _uiState.asStateFlow()
    
    private var breathingJob: Job? = null
    private var currentPhaseTime = 0f
    private var totalPhaseTime = 0f
    
    fun selectExerciseType(type: BreathingExerciseType) {
        _uiState.value = _uiState.value.copy(exerciseType = type)
    }
    
    fun setTargetRounds(rounds: Int) {
        _uiState.value = _uiState.value.copy(targetRounds = rounds)
    }
    
    fun setMoodBefore(mood: Int) {
        _uiState.value = _uiState.value.copy(moodBefore = mood)
    }
    
    fun setMoodAfter(mood: Int) {
        _uiState.value = _uiState.value.copy(moodAfter = mood)
    }
    
    fun startExercise() {
        val currentState = _uiState.value
        
        _uiState.value = currentState.copy(
            isActive = true,
            isCompleted = false,
            currentPhase = BreathingPhase.INHALE,
            completedRounds = 0,
            phaseProgress = 0f,
            startTime = System.currentTimeMillis()
        )
        
        startBreathingCycle()
    }
    
    fun pauseExercise() {
        breathingJob?.cancel()
        _uiState.value = _uiState.value.copy(isActive = false)
    }
    
    fun stopExercise() {
        breathingJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isActive = false,
            currentPhase = BreathingPhase.COMPLETE,
            completedRounds = 0,
            phaseProgress = 0f
        )
    }
    
    fun saveSession() {
        val currentState = _uiState.value
        
        if (currentState.isCompleted && currentState.startTime != null) {
            val session = BreathingSession(
                userId = "local_user", // For single-user local app
                exerciseType = currentState.exerciseType.name.lowercase().replace("_", "-"),
                duration = ((System.currentTimeMillis() - currentState.startTime) / 1000).toInt(),
                completedRounds = currentState.completedRounds,
                targetRounds = currentState.targetRounds,
                stressBefore = null, // Could add stress rating later
                stressAfter = null,
                moodBefore = currentState.moodBefore,
                moodAfter = currentState.moodAfter,
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                notes = ""
            )
            
            viewModelScope.launch {
                try {
                    breathingSessionRepository.insertBreathingSession(session)
                } catch (e: Exception) {
                    // Handle error - could show a snackbar or log
                }
            }
        }
    }
    
    private fun startBreathingCycle() {
        breathingJob = viewModelScope.launch {
            val phases = listOf(
                BreathingPhase.INHALE,
                BreathingPhase.HOLD_INHALE,
                BreathingPhase.EXHALE,
                BreathingPhase.HOLD_EXHALE
            )
            
            while (_uiState.value.isActive && _uiState.value.completedRounds < _uiState.value.targetRounds) {
                for (phase in phases) {
                    if (!_uiState.value.isActive) break
                    
                    _uiState.value = _uiState.value.copy(currentPhase = phase)
                    
                    val phaseDuration = getPhaseDuration(phase, _uiState.value.exerciseType)
                    totalPhaseTime = phaseDuration
                    currentPhaseTime = 0f
                    
                    // Animate through the phase
                    while (currentPhaseTime < totalPhaseTime && _uiState.value.isActive) {
                        val progress = currentPhaseTime / totalPhaseTime
                        _uiState.value = _uiState.value.copy(phaseProgress = progress)
                        
                        delay(50) // Update every 50ms for smooth animation
                        currentPhaseTime += 0.05f
                    }
                    
                    // Complete the phase
                    _uiState.value = _uiState.value.copy(phaseProgress = 1f)
                    delay(100)
                }
                
                // Complete one round
                val newCompletedRounds = _uiState.value.completedRounds + 1
                _uiState.value = _uiState.value.copy(completedRounds = newCompletedRounds)
                
                // Check if we've completed all rounds
                if (newCompletedRounds >= _uiState.value.targetRounds) {
                    _uiState.value = _uiState.value.copy(
                        isActive = false,
                        isCompleted = true,
                        currentPhase = BreathingPhase.COMPLETE
                    )
                    break
                }
            }
        }
    }
    
    private fun getPhaseDuration(phase: BreathingPhase, exerciseType: BreathingExerciseType): Float {
        return when (exerciseType) {
            BreathingExerciseType.BOX -> 4f // 4 seconds for all phases
            BreathingExerciseType.FOUR_SEVEN_EIGHT -> {
                when (phase) {
                    BreathingPhase.INHALE -> 4f
                    BreathingPhase.HOLD_INHALE -> 7f
                    BreathingPhase.EXHALE -> 8f
                    BreathingPhase.HOLD_EXHALE -> 0f // No hold after exhale
                    BreathingPhase.COMPLETE -> 0f
                }
            }
            BreathingExerciseType.SIMPLE -> {
                when (phase) {
                    BreathingPhase.INHALE -> 4f
                    BreathingPhase.HOLD_INHALE -> 0f // No hold
                    BreathingPhase.EXHALE -> 6f
                    BreathingPhase.HOLD_EXHALE -> 0f // No hold
                    BreathingPhase.COMPLETE -> 0f
                }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        breathingJob?.cancel()
    }
}

data class BreathingUiState(
    val isActive: Boolean = false,
    val isCompleted: Boolean = false,
    val currentPhase: BreathingPhase = BreathingPhase.COMPLETE,
    val phaseProgress: Float = 0f,
    val completedRounds: Int = 0,
    val targetRounds: Int = 5,
    val exerciseType: BreathingExerciseType = BreathingExerciseType.BOX,
    val moodBefore: Int? = null,
    val moodAfter: Int? = null,
    val startTime: Long? = null
)
