package com.steadymate.app.ui.theme.accessibility

import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing all accessibility preferences for SteadyMate
 */
data class AccessibilityPreferences(
    val isLargeFont: Boolean = false,
    val isHighContrast: Boolean = false,
    val isReducedMotion: Boolean = false,
    val isDarkMode: Boolean = false,
    val isDynamicColor: Boolean = true,
    val largeTouchTargets: Boolean = false
) {
    companion object {
        fun fromSystemSettings(context: Context): AccessibilityPreferences {
            val fontScale = Settings.System.getFloat(
                context.contentResolver,
                Settings.System.FONT_SCALE,
                1.0f
            )
            
            val isReducedMotionEnabled = try {
                Settings.Global.getFloat(
                    context.contentResolver,
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
                ) == 0f
            } catch (e: Settings.SettingNotFoundException) {
                false
            }

            return AccessibilityPreferences(
                isLargeFont = fontScale >= 1.3f,
                isReducedMotion = isReducedMotionEnabled,
                largeTouchTargets = fontScale >= 1.15f
            )
        }
    }
}

/**
 * Manager for accessibility preferences with system integration
 */
class AccessibilityPreferencesManager(private val context: Context) {
    private val _preferences = MutableStateFlow(
        AccessibilityPreferences.fromSystemSettings(context)
    )
    val preferences: StateFlow<AccessibilityPreferences> = _preferences.asStateFlow()

    fun updatePreferences(update: AccessibilityPreferences.() -> AccessibilityPreferences) {
        _preferences.value = _preferences.value.update()
    }

    fun updateLargeFont(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isLargeFont = enabled)
    }

    fun updateHighContrast(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isHighContrast = enabled)
    }

    fun updateReducedMotion(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isReducedMotion = enabled)
    }

    fun updateDarkMode(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isDarkMode = enabled)
    }

    fun updateDynamicColor(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isDynamicColor = enabled)
    }

    fun updateLargeTouchTargets(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(largeTouchTargets = enabled)
    }

    fun refreshFromSystem() {
        _preferences.value = AccessibilityPreferences.fromSystemSettings(context)
    }
}

/**
 * Composable function to get accessibility preferences
 */
@Composable
fun rememberAccessibilityPreferences(): AccessibilityPreferences {
    val context = LocalContext.current
    val manager = remember { AccessibilityPreferencesManager(context) }
    return manager.preferences.collectAsState().value
}
