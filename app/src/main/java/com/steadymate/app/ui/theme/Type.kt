package com.steadymate.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.steadymate.app.ui.theme.accessibility.AccessibilityPreferences
import com.steadymate.app.ui.theme.accessibility.rememberAccessibilityPreferences

// Base typography with accessibility considerations
val BaseTypography = Typography(
    // Display styles
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline styles
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Title styles
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body styles - optimized for readability
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label styles
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Masculine Typography - Bold, confident, assertive
val MasculineTypography = Typography(
    // Display styles - Bold and commanding
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,        // Extra bold for impact
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.5).sp            // Tighter spacing for more intensity
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline styles - Authoritative and strong
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Title styles - Confident and clear
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,      // Stronger than Medium
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body styles - Readable but strong
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,        // Slightly bolder than Normal
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp                // Slightly tighter
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    
    // Label styles - Sharp and defined
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,          // Bolder for stronger presence
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Large font typography for accessibility
val LargeFontTypography = Typography(
    // Display styles - 1.3x scale
    displayLarge = BaseTypography.displayLarge.copy(
        fontSize = (57 * 1.3f).sp,
        lineHeight = (64 * 1.3f).sp
    ),
    displayMedium = BaseTypography.displayMedium.copy(
        fontSize = (45 * 1.3f).sp,
        lineHeight = (52 * 1.3f).sp
    ),
    displaySmall = BaseTypography.displaySmall.copy(
        fontSize = (36 * 1.3f).sp,
        lineHeight = (44 * 1.3f).sp
    ),
    
    // Headlines
    headlineLarge = BaseTypography.headlineLarge.copy(
        fontSize = (32 * 1.3f).sp,
        lineHeight = (40 * 1.3f).sp
    ),
    headlineMedium = BaseTypography.headlineMedium.copy(
        fontSize = (28 * 1.3f).sp,
        lineHeight = (36 * 1.3f).sp
    ),
    headlineSmall = BaseTypography.headlineSmall.copy(
        fontSize = (24 * 1.3f).sp,
        lineHeight = (32 * 1.3f).sp
    ),
    
    // Titles
    titleLarge = BaseTypography.titleLarge.copy(
        fontSize = (22 * 1.3f).sp,
        lineHeight = (28 * 1.3f).sp
    ),
    titleMedium = BaseTypography.titleMedium.copy(
        fontSize = (16 * 1.3f).sp,
        lineHeight = (24 * 1.3f).sp
    ),
    titleSmall = BaseTypography.titleSmall.copy(
        fontSize = (14 * 1.3f).sp,
        lineHeight = (20 * 1.3f).sp
    ),
    
    // Body - most important for readability
    bodyLarge = BaseTypography.bodyLarge.copy(
        fontSize = (16 * 1.3f).sp,
        lineHeight = (24 * 1.3f).sp
    ),
    bodyMedium = BaseTypography.bodyMedium.copy(
        fontSize = (14 * 1.3f).sp,
        lineHeight = (20 * 1.3f).sp
    ),
    bodySmall = BaseTypography.bodySmall.copy(
        fontSize = (12 * 1.3f).sp,
        lineHeight = (16 * 1.3f).sp
    ),
    
    // Labels
    labelLarge = BaseTypography.labelLarge.copy(
        fontSize = (14 * 1.3f).sp,
        lineHeight = (20 * 1.3f).sp
    ),
    labelMedium = BaseTypography.labelMedium.copy(
        fontSize = (12 * 1.3f).sp,
        lineHeight = (16 * 1.3f).sp
    ),
    labelSmall = BaseTypography.labelSmall.copy(
        fontSize = (11 * 1.3f).sp,
        lineHeight = (16 * 1.3f).sp
    )
)

/**
 * Composable function that returns the appropriate typography based on accessibility preferences
 */
@Composable
fun getAccessibleTypography(preferences: AccessibilityPreferences? = null): Typography {
    val accessibilityPrefs = preferences ?: rememberAccessibilityPreferences()
    
    return if (accessibilityPrefs.isLargeFont) {
        LargeFontTypography
    } else {
        BaseTypography
    }
}

/**
 * Legacy typography for backward compatibility
 */
val Typography = BaseTypography

/**
 * Helper function to scale text size based on accessibility preferences
 */
fun TextUnit.scaleForAccessibility(preferences: AccessibilityPreferences): TextUnit {
    return if (preferences.isLargeFont) {
        this * 1.3f
    } else {
        this
    }
}
