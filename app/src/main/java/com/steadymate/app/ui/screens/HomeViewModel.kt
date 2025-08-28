package com.steadymate.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.domain.model.User
import com.steadymate.app.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 * This demonstrates proper ViewModel structure with use case integration and state management.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        getCurrentUserUseCase()
            .onEach { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }
}

/**
 * UI state for the Home screen.
 * This represents all the data needed by the UI.
 */
data class HomeUiState(
    val currentUser: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
