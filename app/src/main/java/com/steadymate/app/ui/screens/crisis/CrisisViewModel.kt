package com.steadymate.app.ui.screens.crisis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Crisis overlay screen
 */
@HiltViewModel
class CrisisViewModel @Inject constructor(
    // Add repository dependencies here when available
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CrisisUiState())
    val uiState: StateFlow<CrisisUiState> = _uiState.asStateFlow()
    
    init {
        loadCrisisResources()
    }
    
    private fun loadCrisisResources() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // TODO: Load crisis resources from repository
                val resources = listOf(
                    CrisisResource(
                        title = "National Suicide Prevention Lifeline",
                        phoneNumber = "988",
                        description = "Free and confidential emotional support 24/7"
                    ),
                    CrisisResource(
                        title = "Crisis Text Line",
                        textNumber = "741741",
                        description = "Text HOME to 741741 for crisis support"
                    ),
                    CrisisResource(
                        title = "SAMHSA Helpline",
                        phoneNumber = "1-800-662-4357",
                        description = "Treatment referral and information service"
                    )
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    crisisResources = resources,
                    safetyPlan = loadSafetyPlan(),
                    trustedContacts = loadTrustedContacts()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    fun callHelpline(phoneNumber: String) {
        viewModelScope.launch {
            // TODO: Trigger phone call intent
        }
    }
    
    fun sendSms(textNumber: String, message: String = "HOME") {
        viewModelScope.launch {
            // TODO: Trigger SMS intent
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun updateSafetyPlan(safetyPlan: SafetyPlan) {
        _uiState.value = _uiState.value.copy(safetyPlan = safetyPlan)
        // TODO: Save to repository/storage
    }
    
    fun addTrustedContact(contact: TrustedContact) {
        val updatedContacts = _uiState.value.trustedContacts + contact
        _uiState.value = _uiState.value.copy(trustedContacts = updatedContacts)
        // TODO: Save to repository/storage
    }
    
    fun removeTrustedContact(contact: TrustedContact) {
        val updatedContacts = _uiState.value.trustedContacts.filterNot { it.id == contact.id }
        _uiState.value = _uiState.value.copy(trustedContacts = updatedContacts)
        // TODO: Save to repository/storage
    }
    
    private fun loadSafetyPlan(): SafetyPlan {
        // TODO: Load from repository/storage
        return SafetyPlan(
            warningSigns = listOf(
                "Feeling overwhelmed or hopeless",
                "Isolating from friends and family",
                "Having difficulty sleeping"
            ),
            copingStrategies = listOf(
                "Take deep breaths",
                "Call a trusted friend",
                "Use breathing exercises in the app"
            ),
            safeEnvironmentSteps = listOf(
                "Remove harmful items from immediate vicinity",
                "Go to a public space",
                "Stay with someone I trust"
            )
        )
    }
    
    private fun loadTrustedContacts(): List<TrustedContact> {
        // TODO: Load from repository/storage
        return emptyList()
    }
}

/**
 * UI state for Crisis screen
 */
data class CrisisUiState(
    val isLoading: Boolean = false,
    val crisisResources: List<CrisisResource> = emptyList(),
    val safetyPlan: SafetyPlan = SafetyPlan(),
    val trustedContacts: List<TrustedContact> = emptyList(),
    val errorMessage: String? = null
)

/**
 * Crisis resource data class
 */
data class CrisisResource(
    val title: String,
    val phoneNumber: String? = null,
    val textNumber: String? = null,
    val description: String,
    val isAvailable24x7: Boolean = true
)

/**
 * Safety plan data class
 */
data class SafetyPlan(
    val warningSigns: List<String> = emptyList(),
    val copingStrategies: List<String> = emptyList(),
    val safeEnvironmentSteps: List<String> = emptyList(),
    val distractionActivities: List<String> = emptyList()
)

/**
 * Trusted contact data class
 */
data class TrustedContact(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val phoneNumber: String,
    val relationship: String = "",
    val isEmergencyContact: Boolean = false
)
