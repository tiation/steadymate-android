package com.steadymate.app.ui.theme.accessibility

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Accessibility touch target sizes following Material Design guidelines
 */
object TouchTarget {
    val MINIMUM = 48.dp  // Material Design minimum touch target
    val LARGE = 56.dp    // Large touch target for accessibility
    val EXTRA_LARGE = 64.dp  // Extra large for severe motor impairments
}

/**
 * Extension function to apply large touch target sizing for accessibility
 * 
 * @param enabled Whether to apply large touch targets
 * @param minSize Minimum size for the touch target
 */
fun Modifier.largeTouchTarget(
    enabled: Boolean = false,
    minSize: Dp = if (enabled) TouchTarget.LARGE else TouchTarget.MINIMUM
): Modifier {
    return if (enabled) {
        this.then(Modifier.defaultMinSize(minWidth = minSize, minHeight = minSize))
    } else {
        this.then(Modifier.defaultMinSize(minWidth = TouchTarget.MINIMUM, minHeight = TouchTarget.MINIMUM))
    }
}

/**
 * Composable extension that automatically applies touch target sizing based on accessibility preferences
 */
@Composable
fun Modifier.accessibleTouchTarget(
    customSize: Dp? = null
): Modifier {
    val preferences = rememberAccessibilityPreferences()
    val targetSize = customSize ?: when {
        preferences.largeTouchTargets && preferences.isLargeFont -> TouchTarget.EXTRA_LARGE
        preferences.largeTouchTargets -> TouchTarget.LARGE
        else -> TouchTarget.MINIMUM
    }
    
    return this.then(Modifier.defaultMinSize(minWidth = targetSize, minHeight = targetSize))
}

/**
 * Creates a size modifier that respects accessibility preferences
 */
@Composable
fun Modifier.accessibleSize(
    baseSize: Dp,
    largeSize: Dp = baseSize * 1.25f,
    extraLargeSize: Dp = baseSize * 1.5f
): Modifier {
    val preferences = rememberAccessibilityPreferences()
    val targetSize = when {
        preferences.isLargeFont && preferences.largeTouchTargets -> extraLargeSize
        preferences.largeTouchTargets -> largeSize
        else -> baseSize
    }
    
    return this.then(Modifier.size(targetSize))
}
