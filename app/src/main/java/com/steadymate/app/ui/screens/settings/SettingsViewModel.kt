package com.steadymate.app.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.data.repository.OnboardingPreferencesRepository
import com.steadymate.app.ui.theme.accessibility.AccessibilityPreferences
import com.steadymate.app.ui.theme.accessibility.AccessibilityPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen with full functionality
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val onboardingPreferencesRepository: OnboardingPreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private lateinit var accessibilityManager: AccessibilityPreferencesManager
    
    init {
        loadSettings()
    }
    
    fun initializeAccessibilityManager(context: Context) {
        if (!::accessibilityManager.isInitialized) {
            accessibilityManager = AccessibilityPreferencesManager(context)
            observeAccessibilityPreferences()
        }
    }
    
    private fun observeAccessibilityPreferences() {
        if (::accessibilityManager.isInitialized) {
            viewModelScope.launch {
                accessibilityManager.preferences.collect { accessibilityPrefs ->
                    _uiState.value = _uiState.value.copy(
                        accessibilityPreferences = accessibilityPrefs
                    )
                }
            }
        }
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Observe preferences from repository
                combine(
                    onboardingPreferencesRepository.getThemeSettings(),
                    onboardingPreferencesRepository.getNotificationSettings(),
                    onboardingPreferencesRepository.getConsentSettings(),
                    onboardingPreferencesRepository.getColorPalette()
                ) { theme, notifications, consent, colorPalette ->
                    SettingsUiState(
                        isLoading = false,
                        userPreferences = UserPreferences(
                            notificationsEnabled = notifications.pushNotificationsEnabled,
                            darkThemeEnabled = theme.mode == com.steadymate.app.data.proto.Theme.ThemeMode.DARK || 
                                             theme.mode == com.steadymate.app.data.proto.Theme.ThemeMode.THEME_MODE_UNSPECIFIED,
                            crisisHotlineEnabled = true, // Default to true for safety
                            dataBackupEnabled = true, // Default preference
                            analyticsEnabled = consent.analyticsConsent
                        ),
                        accessibilityPreferences = _uiState.value.accessibilityPreferences,
                        currentColorPalette = colorPalette,
                        appVersion = getAppVersion(),
                        buildNumber = getBuildNumber()
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load settings"
                )
            }
        }
    }
    
    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentPrefs = _uiState.value.userPreferences
                val updatedPrefs = currentPrefs.copy(notificationsEnabled = enabled)
                _uiState.value = _uiState.value.copy(userPreferences = updatedPrefs)
                
                // Update in repository
                val currentNotifications = onboardingPreferencesRepository.getNotificationSettings().first()
                val updatedNotifications = currentNotifications.toBuilder()
                    .setPushNotificationsEnabled(enabled)
                    .build()
                onboardingPreferencesRepository.updateNotificationSettings(updatedNotifications)
            } catch (e: Exception) {
                showError("Failed to update notification settings: ${e.message}")
            }
        }
    }
    
    fun updateDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentPrefs = _uiState.value.userPreferences
                val updatedPrefs = currentPrefs.copy(darkThemeEnabled = enabled)
                _uiState.value = _uiState.value.copy(userPreferences = updatedPrefs)
                
                // Update in repository
                val currentTheme = onboardingPreferencesRepository.getThemeSettings().first()
                val updatedTheme = currentTheme.toBuilder()
                    .setMode(
                        if (enabled) com.steadymate.app.data.proto.Theme.ThemeMode.DARK
                        else com.steadymate.app.data.proto.Theme.ThemeMode.LIGHT
                    )
                    .build()
                onboardingPreferencesRepository.updateTheme(updatedTheme)
            } catch (e: Exception) {
                showError("Failed to update theme settings: ${e.message}")
            }
        }
    }
    
    fun updateColorPalette(colorPalette: com.steadymate.app.data.proto.Theme.ColorPalette) {
        viewModelScope.launch {
            try {
                onboardingPreferencesRepository.updateColorPalette(colorPalette)
            } catch (e: Exception) {
                showError("Failed to update color palette: ${e.message}")
            }
        }
    }
    
    fun updateHighContrast(enabled: Boolean) {
        if (::accessibilityManager.isInitialized) {
            accessibilityManager.updateHighContrast(enabled)
        }
    }
    
    fun updateLargeFont(enabled: Boolean) {
        if (::accessibilityManager.isInitialized) {
            accessibilityManager.updateLargeFont(enabled)
        }
    }
    
    fun updateReducedMotion(enabled: Boolean) {
        if (::accessibilityManager.isInitialized) {
            accessibilityManager.updateReducedMotion(enabled)
        }
    }
    
    fun updateDynamicColor(enabled: Boolean) {
        if (::accessibilityManager.isInitialized) {
            accessibilityManager.updateDynamicColor(enabled)
        }
    }
    
    fun updateCrisisHotlineEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentPrefs = _uiState.value.userPreferences
            val updatedPrefs = currentPrefs.copy(crisisHotlineEnabled = enabled)
            _uiState.value = _uiState.value.copy(userPreferences = updatedPrefs)
            
            // This is a local preference, no repository save needed
        }
    }
    
    fun updateDataBackupEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentPrefs = _uiState.value.userPreferences
            val updatedPrefs = currentPrefs.copy(dataBackupEnabled = enabled)
            _uiState.value = _uiState.value.copy(userPreferences = updatedPrefs)
            
            // TODO: Implement backup functionality
        }
    }
    
    fun updateAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentPrefs = _uiState.value.userPreferences
                val updatedPrefs = currentPrefs.copy(analyticsEnabled = enabled)
                _uiState.value = _uiState.value.copy(userPreferences = updatedPrefs)
                
                // Update in repository
                val currentConsent = onboardingPreferencesRepository.getConsentSettings().first()
                val updatedConsent = currentConsent.toBuilder()
                    .setAnalyticsConsent(enabled)
                    .setConsentTimestamp(System.currentTimeMillis())
                    .build()
                onboardingPreferencesRepository.updateConsentSettings(updatedConsent)
            } catch (e: Exception) {
                showError("Failed to update analytics settings: ${e.message}")
            }
        }
    }
    
    fun clearAppData(context: Context) {
        viewModelScope.launch {
            try {
                // Reset onboarding to clear all data
                onboardingPreferencesRepository.resetOnboarding()
                
                // Clear accessibility preferences
                if (::accessibilityManager.isInitialized) {
                    accessibilityManager.refreshFromSystem()
                }
                
                // Reload settings
                loadSettings()
                
                showSuccess("App data cleared successfully")
            } catch (e: Exception) {
                showError("Failed to clear app data: ${e.message}")
            }
        }
    }
    
    fun exportUserData(context: Context) {
        viewModelScope.launch {
            try {
                // Create export data
                val exportData = buildString {
                    appendLine("SteadyMate User Data Export")
                    appendLine("Generated: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
                    appendLine()
                    
                    appendLine("User Preferences:")
                    val prefs = _uiState.value.userPreferences
                    appendLine("- Notifications: ${prefs.notificationsEnabled}")
                    appendLine("- Dark Theme: ${prefs.darkThemeEnabled}")
                    appendLine("- Crisis Hotline: ${prefs.crisisHotlineEnabled}")
                    appendLine("- Data Backup: ${prefs.dataBackupEnabled}")
                    appendLine("- Analytics: ${prefs.analyticsEnabled}")
                    appendLine()
                    
                    appendLine("Accessibility Preferences:")
                    val accessPrefs = _uiState.value.accessibilityPreferences
                    appendLine("- Large Font: ${accessPrefs.isLargeFont}")
                    appendLine("- High Contrast: ${accessPrefs.isHighContrast}")
                    appendLine("- Reduced Motion: ${accessPrefs.isReducedMotion}")
                    appendLine("- Dynamic Color: ${accessPrefs.isDynamicColor}")
                }
                
                // Create sharing intent
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, exportData)
                    putExtra(Intent.EXTRA_SUBJECT, "SteadyMate Data Export")
                }
                
                val chooserIntent = Intent.createChooser(shareIntent, "Export Data")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
                
                showSuccess("Data export started")
            } catch (e: Exception) {
                showError("Failed to export data: ${e.message}")
            }
        }
    }
    
    fun resetOnboarding() {
        viewModelScope.launch {
            try {
                onboardingPreferencesRepository.resetOnboarding()
                showSuccess("Onboarding reset. Please restart the app.")
            } catch (e: Exception) {
                showError("Failed to reset onboarding: ${e.message}")
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
    
    private fun showError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
    
    private fun showSuccess(message: String) {
        _uiState.value = _uiState.value.copy(successMessage = message)
    }
    
    private fun getAppVersion(): String {
        return "1.0.0" // TODO: Get from BuildConfig
    }
    
    private fun getBuildNumber(): String {
        return "1" // TODO: Get from BuildConfig
    }
}

/**
 * UI state for Settings screen with comprehensive data
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val userPreferences: UserPreferences = UserPreferences(),
    val accessibilityPreferences: AccessibilityPreferences = AccessibilityPreferences(),
    val currentColorPalette: com.steadymate.app.data.proto.Theme.ColorPalette = com.steadymate.app.data.proto.Theme.ColorPalette.BEAUTIFUL,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val appVersion: String = "1.0.0",
    val buildNumber: String = "1"
)

/**
 * Enhanced user preferences data class
 */
data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val darkThemeEnabled: Boolean = false,
    val crisisHotlineEnabled: Boolean = true,
    val dataBackupEnabled: Boolean = true,
    val analyticsEnabled: Boolean = false
)
