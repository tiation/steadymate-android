package com.steadymate.app.ui.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized haptics manager for consistent vibration patterns across the app
 */
@Singleton
class HapticsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
    
    /**
     * Check if haptics are available on this device
     */
    val isHapticsAvailable: Boolean
        get() = vibrator?.hasVibrator() == true
    
    /**
     * Breathing exercise haptic patterns
     */
    object BreathingPatterns {
        val INHALE_START = HapticPattern.SHORT_PULSE
        val EXHALE_START = HapticPattern.DOUBLE_PULSE
        val HOLD_START = HapticPattern.GENTLE_PULSE
        val CYCLE_COMPLETE = HapticPattern.SUCCESS
        val SESSION_COMPLETE = HapticPattern.CELEBRATION
    }
    
    /**
     * UI interaction haptic patterns
     */
    object UIPatterns {
        val BUTTON_PRESS = HapticPattern.GENTLE_PULSE
        val SELECTION = HapticPattern.SHORT_PULSE
        val TOGGLE_ON = HapticPattern.SUCCESS
        val TOGGLE_OFF = HapticPattern.GENTLE_PULSE
        val ERROR = HapticPattern.ERROR
        val SUCCESS = HapticPattern.SUCCESS
        val WARNING = HapticPattern.WARNING
    }
    
    /**
     * Perform a haptic feedback with the specified pattern
     */
    fun performHaptic(pattern: HapticPattern) {
        if (!isHapticsAvailable) return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                performModernHaptic(pattern)
            } else {
                performLegacyHaptic(pattern)
            }
        } catch (e: Exception) {
            // Gracefully handle any vibration errors
            android.util.Log.w("HapticsHelper", "Failed to perform haptic feedback", e)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun performModernHaptic(pattern: HapticPattern) {
        val effect = when (pattern) {
            HapticPattern.GENTLE_PULSE -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE / 2)
            HapticPattern.SHORT_PULSE -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            HapticPattern.DOUBLE_PULSE -> VibrationEffect.createWaveform(
                longArrayOf(0, 80, 50, 80), 
                intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE / 2, 0, VibrationEffect.DEFAULT_AMPLITUDE / 2),
                -1
            )
            HapticPattern.SUCCESS -> VibrationEffect.createWaveform(
                longArrayOf(0, 50, 50, 100),
                intArrayOf(0, 150, 0, 200),
                -1
            )
            HapticPattern.ERROR -> VibrationEffect.createWaveform(
                longArrayOf(0, 100, 50, 100, 50, 100),
                intArrayOf(0, 255, 0, 255, 0, 255),
                -1
            )
            HapticPattern.WARNING -> VibrationEffect.createWaveform(
                longArrayOf(0, 150, 100, 150),
                intArrayOf(0, 180, 0, 180),
                -1
            )
            HapticPattern.CELEBRATION -> VibrationEffect.createWaveform(
                longArrayOf(0, 50, 30, 50, 30, 100, 50, 50),
                intArrayOf(0, 120, 0, 150, 0, 200, 0, 180),
                -1
            )
        }
        
        vibrator?.vibrate(effect)
    }
    
    @Suppress("DEPRECATION")
    private fun performLegacyHaptic(pattern: HapticPattern) {
        val duration = when (pattern) {
            HapticPattern.GENTLE_PULSE -> 50L
            HapticPattern.SHORT_PULSE -> 100L
            HapticPattern.DOUBLE_PULSE -> 150L
            HapticPattern.SUCCESS -> 200L
            HapticPattern.ERROR -> 300L
            HapticPattern.WARNING -> 250L
            HapticPattern.CELEBRATION -> 400L
        }
        
        vibrator?.vibrate(duration)
    }
    
    /**
     * Perform breathing-specific haptic feedback
     */
    fun performBreathingHaptic(phase: com.steadymate.app.domain.model.BreathingPhase) {
        val pattern = when (phase) {
            com.steadymate.app.domain.model.BreathingPhase.INHALE -> BreathingPatterns.INHALE_START
            com.steadymate.app.domain.model.BreathingPhase.HOLD_INHALE -> BreathingPatterns.HOLD_START
            com.steadymate.app.domain.model.BreathingPhase.EXHALE -> BreathingPatterns.EXHALE_START
            com.steadymate.app.domain.model.BreathingPhase.HOLD_EXHALE -> BreathingPatterns.HOLD_START
            com.steadymate.app.domain.model.BreathingPhase.COMPLETE -> BreathingPatterns.SESSION_COMPLETE
        }
        performHaptic(pattern)
    }
    
    /**
     * Perform UI interaction haptic feedback
     */
    fun performUIHaptic(interaction: UIInteraction) {
        val pattern = when (interaction) {
            UIInteraction.BUTTON_PRESS -> UIPatterns.BUTTON_PRESS
            UIInteraction.SELECTION -> UIPatterns.SELECTION
            UIInteraction.TOGGLE_ON -> UIPatterns.TOGGLE_ON
            UIInteraction.TOGGLE_OFF -> UIPatterns.TOGGLE_OFF
            UIInteraction.ERROR -> UIPatterns.ERROR
            UIInteraction.SUCCESS -> UIPatterns.SUCCESS
            UIInteraction.WARNING -> UIPatterns.WARNING
        }
        performHaptic(pattern)
    }
}

/**
 * Predefined haptic patterns for different feedback types
 */
enum class HapticPattern {
    GENTLE_PULSE,
    SHORT_PULSE,
    DOUBLE_PULSE,
    SUCCESS,
    ERROR,
    WARNING,
    CELEBRATION
}

/**
 * UI interaction types that trigger haptic feedback
 */
enum class UIInteraction {
    BUTTON_PRESS,
    SELECTION,
    TOGGLE_ON,
    TOGGLE_OFF,
    ERROR,
    SUCCESS,
    WARNING
}

/**
 * Composable function to get haptics helper in Compose contexts
 */
@Composable
fun rememberHapticsHelper(): HapticsHelper {
    val context = LocalContext.current
    return remember { HapticsHelper(context) }
}

/**
 * Extension function for easier haptic feedback in Compose
 */
@Composable
fun performHaptic(pattern: HapticPattern) {
    val hapticsHelper = rememberHapticsHelper()
    hapticsHelper.performHaptic(pattern)
}
