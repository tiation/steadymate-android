package com.steadymate.app.ui.theme.accessibility

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * Animation specifications that respect accessibility preferences
 */
object AccessibleAnimations {
    
    /**
     * Standard duration for animations when motion is enabled
     */
    const val STANDARD_DURATION = 300
    const val QUICK_DURATION = 150
    const val SLOW_DURATION = 500
    
    /**
     * Reduced motion duration - much faster or instant
     */
    const val REDUCED_MOTION_DURATION = 50
    const val INSTANT_DURATION = 0
    
    /**
     * Returns appropriate animation duration based on accessibility preferences
     */
    @Composable
    fun getDuration(
        standardDuration: Int = STANDARD_DURATION,
        preferences: AccessibilityPreferences? = null
    ): Int {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        return if (accessibilityPrefs.isReducedMotion) {
            REDUCED_MOTION_DURATION
        } else {
            standardDuration
        }
    }
    
    /**
     * Returns appropriate animation spec based on accessibility preferences
     */
    @Composable
    fun <T> getAnimationSpec(
        standardSpec: AnimationSpec<T>? = null,
        preferences: AccessibilityPreferences? = null
    ): AnimationSpec<T> {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        
        return if (accessibilityPrefs.isReducedMotion) {
            tween(durationMillis = REDUCED_MOTION_DURATION)
        } else {
            standardSpec ?: spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        }
    }
    
    /**
     * Accessible fade in animation
     */
    @Composable
    fun accessibleFadeIn(
        preferences: AccessibilityPreferences? = null
    ): EnterTransition {
        val duration = getDuration(STANDARD_DURATION, preferences)
        return fadeIn(animationSpec = tween(duration))
    }
    
    /**
     * Accessible fade out animation
     */
    @Composable
    fun accessibleFadeOut(
        preferences: AccessibilityPreferences? = null
    ): ExitTransition {
        val duration = getDuration(STANDARD_DURATION, preferences)
        return fadeOut(animationSpec = tween(duration))
    }
    
    /**
     * Accessible slide in from left
     */
    @Composable
    fun accessibleSlideInLeft(
        preferences: AccessibilityPreferences? = null
    ): EnterTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        val duration = getDuration(STANDARD_DURATION, preferences)
        
        return if (accessibilityPrefs.isReducedMotion) {
            fadeIn(animationSpec = tween(duration))
        } else {
            slideInHorizontally(
                animationSpec = tween(duration),
                initialOffsetX = { -it }
            )
        }
    }
    
    /**
     * Accessible slide out to right
     */
    @Composable
    fun accessibleSlideOutRight(
        preferences: AccessibilityPreferences? = null
    ): ExitTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        val duration = getDuration(STANDARD_DURATION, preferences)
        
        return if (accessibilityPrefs.isReducedMotion) {
            fadeOut(animationSpec = tween(duration))
        } else {
            slideOutHorizontally(
                animationSpec = tween(duration),
                targetOffsetX = { it }
            )
        }
    }
    
    /**
     * Accessible slide in from bottom
     */
    @Composable
    fun accessibleSlideInBottom(
        preferences: AccessibilityPreferences? = null
    ): EnterTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        val duration = getDuration(STANDARD_DURATION, preferences)
        
        return if (accessibilityPrefs.isReducedMotion) {
            fadeIn(animationSpec = tween(duration))
        } else {
            slideInVertically(
                animationSpec = tween(duration),
                initialOffsetY = { it }
            )
        }
    }
    
    /**
     * Accessible slide out to bottom
     */
    @Composable
    fun accessibleSlideOutBottom(
        preferences: AccessibilityPreferences? = null
    ): ExitTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        val duration = getDuration(STANDARD_DURATION, preferences)
        
        return if (accessibilityPrefs.isReducedMotion) {
            fadeOut(animationSpec = tween(duration))
        } else {
            slideOutVertically(
                animationSpec = tween(duration),
                targetOffsetY = { it }
            )
        }
    }
    
    /**
     * Accessible scale in animation
     */
    @Composable
    fun accessibleScaleIn(
        preferences: AccessibilityPreferences? = null
    ): EnterTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        val duration = getDuration(STANDARD_DURATION, preferences)
        
        return if (accessibilityPrefs.isReducedMotion) {
            fadeIn(animationSpec = tween(duration))
        } else {
            scaleIn(
                animationSpec = tween(duration),
                initialScale = 0.8f
            )
        }
    }
    
    /**
     * Accessible scale out animation
     */
    @Composable
    fun accessibleScaleOut(
        preferences: AccessibilityPreferences? = null
    ): ExitTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        val duration = getDuration(STANDARD_DURATION, preferences)
        
        return if (accessibilityPrefs.isReducedMotion) {
            fadeOut(animationSpec = tween(duration))
        } else {
            scaleOut(
                animationSpec = tween(duration),
                targetScale = 0.8f
            )
        }
    }
    
    /**
     * Combined enter transition that respects accessibility
     */
    @Composable
    fun accessibleEnterTransition(
        preferences: AccessibilityPreferences? = null
    ): EnterTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        
        return if (accessibilityPrefs.isReducedMotion) {
            accessibleFadeIn(preferences)
        } else {
            accessibleFadeIn(preferences) + accessibleScaleIn(preferences)
        }
    }
    
    /**
     * Combined exit transition that respects accessibility
     */
    @Composable
    fun accessibleExitTransition(
        preferences: AccessibilityPreferences? = null
    ): ExitTransition {
        val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
        
        return if (accessibilityPrefs.isReducedMotion) {
            accessibleFadeOut(preferences)
        } else {
            accessibleFadeOut(preferences) + accessibleScaleOut(preferences)
        }
    }
}

/**
 * Extension functions for AnimatedVisibilityScope that respect accessibility
 */

/**
 * Accessible slide in transition for AnimatedVisibilityScope
 */
@Composable
fun AnimatedVisibilityScope.accessibleSlideInTransition(
    preferences: AccessibilityPreferences? = null
): EnterTransition {
    return AccessibleAnimations.accessibleSlideInLeft(preferences)
}

/**
 * Accessible slide out transition for AnimatedVisibilityScope
 */
@Composable
fun AnimatedVisibilityScope.accessibleSlideOutTransition(
    preferences: AccessibilityPreferences? = null
): ExitTransition {
    return AccessibleAnimations.accessibleSlideOutRight(preferences)
}
