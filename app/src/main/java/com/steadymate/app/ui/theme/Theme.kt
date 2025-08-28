package com.steadymate.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.steadymate.app.ui.theme.accessibility.rememberAccessibilityPreferences

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val HighContrastLight = lightColorScheme(
    primary = HighContrastColors.HCPrimary,
    onPrimary = HighContrastColors.HCOnPrimary,
    primaryContainer = HighContrastColors.HCPrimaryContainer,
    onPrimaryContainer = HighContrastColors.HCOnPrimaryContainer,
    secondary = HighContrastColors.HCSecondary,
    onSecondary = HighContrastColors.HCOnSecondary,
    secondaryContainer = HighContrastColors.HCSecondaryContainer,
    onSecondaryContainer = HighContrastColors.HCOnSecondaryContainer,
    tertiary = HighContrastColors.HCTertiary,
    onTertiary = HighContrastColors.HCOnTertiary,
    tertiaryContainer = HighContrastColors.HCTertiaryContainer,
    onTertiaryContainer = HighContrastColors.HCOnTertiaryContainer,
    error = HighContrastColors.HCError,
    onError = HighContrastColors.HCOnError,
    errorContainer = HighContrastColors.HCErrorContainer,
    onErrorContainer = HighContrastColors.HCOnErrorContainer,
    background = HighContrastColors.HCBackground,
    onBackground = HighContrastColors.HCOnBackground,
    surface = HighContrastColors.HCSurface,
    onSurface = HighContrastColors.HCOnSurface,
    surfaceVariant = HighContrastColors.HCSurfaceVariant,
    onSurfaceVariant = HighContrastColors.HCOnSurfaceVariant,
    outline = HighContrastColors.HCOutline,
    outlineVariant = HighContrastColors.HCOutlineVariant
)

private val HighContrastDark = darkColorScheme(
    primary = HighContrastColors.HCDarkPrimary,
    onPrimary = HighContrastColors.HCDarkOnPrimary,
    primaryContainer = HighContrastColors.HCDarkPrimaryContainer,
    onPrimaryContainer = HighContrastColors.HCDarkOnPrimaryContainer,
    secondary = HighContrastColors.HCDarkSecondary,
    onSecondary = HighContrastColors.HCDarkOnSecondary,
    secondaryContainer = HighContrastColors.HCDarkSecondaryContainer,
    onSecondaryContainer = HighContrastColors.HCDarkOnSecondaryContainer,
    tertiary = HighContrastColors.HCDarkTertiary,
    onTertiary = HighContrastColors.HCDarkOnTertiary,
    tertiaryContainer = HighContrastColors.HCDarkTertiaryContainer,
    onTertiaryContainer = HighContrastColors.HCDarkOnTertiaryContainer,
    error = HighContrastColors.HCDarkError,
    onError = HighContrastColors.HCDarkOnError,
    errorContainer = HighContrastColors.HCDarkErrorContainer,
    onErrorContainer = HighContrastColors.HCDarkOnErrorContainer,
    background = HighContrastColors.HCDarkBackground,
    onBackground = HighContrastColors.HCDarkOnBackground,
    surface = HighContrastColors.HCDarkSurface,
    onSurface = HighContrastColors.HCDarkOnSurface,
    surfaceVariant = HighContrastColors.HCDarkSurfaceVariant,
    onSurfaceVariant = HighContrastColors.HCDarkOnSurfaceVariant,
    outline = HighContrastColors.HCDarkOutline,
    outlineVariant = HighContrastColors.HCDarkOutlineVariant
)

// Masculine Dark Mode Color Scheme - Bold, industrial, assertive
private val MasculineDark = darkColorScheme(
    // Primary colors - Electric blue for confidence and authority
    primary = MasculineDarkColors.ElectricBlue,
    onPrimary = MasculineDarkColors.CarbonBlack,
    primaryContainer = MasculineDarkColors.SteelBlue,
    onPrimaryContainer = MasculineDarkColors.PureWhite,
    
    // Secondary colors - Cyber blue for tech/digital feel
    secondary = MasculineDarkColors.CyberBlue,
    onSecondary = MasculineDarkColors.PureWhite,
    secondaryContainer = MasculineDarkColors.StormGray,
    onSecondaryContainer = MasculineDarkColors.PureWhite,
    
    // Tertiary colors - Amber for warmth and energy
    tertiary = MasculineDarkColors.AmberYellow,
    onTertiary = MasculineDarkColors.CarbonBlack,
    tertiaryContainer = MasculineDarkColors.IronGray,
    onTertiaryContainer = MasculineDarkColors.PureWhite,
    
    // Error colors - Powerful crimson red
    error = MasculineDarkColors.CrimsonRed,
    onError = MasculineDarkColors.PureWhite,
    errorContainer = MasculineDarkColors.RedContainer,
    onErrorContainer = MasculineDarkColors.PureWhite,
    
    // Background and surface colors - Deep, industrial
    background = MasculineDarkColors.DeepCharcoal,
    onBackground = MasculineDarkColors.PureWhite,
    surface = MasculineDarkColors.MidnightBlack,
    onSurface = MasculineDarkColors.PureWhite,
    
    // Surface variants - Layered grays for depth
    surfaceVariant = MasculineDarkColors.GraphiteGray,
    onSurfaceVariant = MasculineDarkColors.SilverGray,
    surfaceTint = MasculineDarkColors.ElectricBlue,
    
    // Outlines - Sharp definition
    outline = MasculineDarkColors.IronGray,
    outlineVariant = MasculineDarkColors.StormGray,
    
    // Inverse colors
    inverseSurface = MasculineDarkColors.PureWhite,
    inverseOnSurface = MasculineDarkColors.DeepCharcoal,
    inversePrimary = MasculineDarkColors.DeepElectric
)

@Composable
fun SteadyMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    largeFont: Boolean? = null,
    highContrast: Boolean? = null,
    reducedMotion: Boolean? = null,
    masculineDark: Boolean = false,
    content: @Composable () -> Unit
) {
    val accessibility = rememberAccessibilityPreferences()

    val useDarkTheme = darkTheme || accessibility.isDarkMode
    val useDynamic = dynamicColor && accessibility.isDynamicColor
    val useHighContrast = (highContrast ?: accessibility.isHighContrast)
    val useMasculineDark = masculineDark

    val colorScheme = when {
        useMasculineDark -> MasculineDark
        useHighContrast && useDarkTheme -> HighContrastDark
        useHighContrast && !useDarkTheme -> HighContrastLight
        useDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            // Light status bar icons when using light theme (dark text/icons), false when dark theme.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !(useDarkTheme || useMasculineDark)
        }
    }

    val typography: Typography = if ((largeFont ?: accessibility.isLargeFont)) getAccessibleTypography(accessibility) else BaseTypography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

/**
 * Masculine Dark Theme - A bold, industrial dark theme with electric blue accents
 * Perfect for users who prefer a more assertive, confident aesthetic
 */
@Composable
fun MasculineDarkTheme(
    largeFont: Boolean? = null,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MasculineDarkColors.MidnightBlack.toArgb()
            // Dark status bar icons for the dark theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    val typography: Typography = if (largeFont == true) {
        // Scale up the masculine typography for accessibility
        MasculineTypography.copy(
            displayLarge = MasculineTypography.displayLarge.copy(fontSize = (57 * 1.3f).sp),
            headlineMedium = MasculineTypography.headlineMedium.copy(fontSize = (28 * 1.3f).sp),
            titleLarge = MasculineTypography.titleLarge.copy(fontSize = (22 * 1.3f).sp),
            bodyLarge = MasculineTypography.bodyLarge.copy(fontSize = (16 * 1.3f).sp)
        )
    } else {
        MasculineTypography
    }

    MaterialTheme(
        colorScheme = MasculineDark,
        typography = typography,
        content = content
    )
}
