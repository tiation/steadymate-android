package com.steadymate.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.data.repository.OnboardingPreferencesRepository
import com.steadymate.app.data.proto.ConsentSettings
import com.steadymate.app.data.proto.NotificationSettings
import com.steadymate.app.data.proto.OnboardingStatus
import com.steadymate.app.data.proto.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingPreferencesRepository
) : ViewModel() {

    // UI State for onboarding flow
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // Combined state from repository and UI state
    val onboardingState = combine(
        onboardingRepository.getOnboardingPrefs(),
        _uiState
    ) { prefs, uiState ->
        OnboardingState(
            preferences = prefs,
            uiState = uiState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OnboardingState()
    )

    init {
        // Initialize onboarding if not started
        viewModelScope.launch {
            val currentPrefs = onboardingRepository.getOnboardingPrefs()
            currentPrefs.collect { prefs ->
                if (prefs.onboardingStatus.startedTimestamp == 0L) {
                    startOnboarding()
                }
            }
        }
    }

    private fun startOnboarding() {
        viewModelScope.launch {
            val startedStatus = OnboardingStatus.newBuilder()
                .setIsCompleted(false)
                .setCurrentStep(0)
                .setTotalSteps(4)
                .setStartedTimestamp(System.currentTimeMillis())
                .build()
            onboardingRepository.updateOnboardingStatus(startedStatus)
        }
    }

    fun markScreenCompleted(screenIndex: Int) {
        viewModelScope.launch {
            onboardingRepository.markScreenCompleted(screenIndex)
        }
    }

    fun updateThemeSettings(
        themeMode: Theme.ThemeMode,
        useDynamicColors: Boolean = false,
        highContrastEnabled: Boolean = false,
        accentColor: String = "",
        colorPalette: Theme.ColorPalette = Theme.ColorPalette.BEAUTIFUL
    ) {
        viewModelScope.launch {
            val theme = Theme.newBuilder()
                .setMode(themeMode)
                .setUseDynamicColors(useDynamicColors)
                .setHighContrastEnabled(highContrastEnabled)
                .setAccentColor(accentColor)
                .setColorPalette(colorPalette)
                .build()
            onboardingRepository.updateTheme(theme)
        }
    }
    
    fun updateColorPalette(colorPalette: Theme.ColorPalette) {
        viewModelScope.launch {
            onboardingRepository.updateColorPalette(colorPalette)
        }
    }

    fun updateConsentSettings(
        analyticsConsent: Boolean,
        crashReportingConsent: Boolean,
        marketingConsent: Boolean,
        dataSharingConsent: Boolean
    ) {
        viewModelScope.launch {
            val consentSettings = ConsentSettings.newBuilder()
                .setAnalyticsConsent(analyticsConsent)
                .setCrashReportingConsent(crashReportingConsent)
                .setMarketingConsent(marketingConsent)
                .setDataSharingConsent(dataSharingConsent)
                .setConsentTimestamp(System.currentTimeMillis())
                .setConsentVersion("1.0")
                .build()
            onboardingRepository.updateConsentSettings(consentSettings)
        }
    }

    fun updateNotificationSettings(
        pushNotifications: Boolean,
        emailNotifications: Boolean,
        workoutReminders: Boolean,
        progressUpdates: Boolean,
        achievementAlerts: Boolean,
        reminderTimes: List<Int> = emptyList(),
        quietHoursEnabled: Boolean = false,
        quietStartHour: Int = 22,
        quietEndHour: Int = 7
    ) {
        viewModelScope.launch {
            val notificationSettings = NotificationSettings.newBuilder()
                .setPushNotificationsEnabled(pushNotifications)
                .setEmailNotificationsEnabled(emailNotifications)
                .setWorkoutRemindersEnabled(workoutReminders)
                .setProgressUpdatesEnabled(progressUpdates)
                .setAchievementAlertsEnabled(achievementAlerts)
                .addAllReminderTimes(reminderTimes)
                .setQuietHoursEnabled(quietHoursEnabled)
                .setQuietStartHour(quietStartHour)
                .setQuietEndHour(quietEndHour)
                .build()
            onboardingRepository.updateNotificationSettings(notificationSettings)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingRepository.completeOnboarding()
        }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI state for onboarding screens
 */
data class OnboardingUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Combined state for onboarding
 */
data class OnboardingState(
    val preferences: com.steadymate.app.data.proto.OnboardingPrefs = 
        com.steadymate.app.data.proto.OnboardingPrefs.getDefaultInstance(),
    val uiState: OnboardingUiState = OnboardingUiState()
)
