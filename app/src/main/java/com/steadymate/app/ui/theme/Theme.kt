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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.steadymate.app.ui.theme.accessibility.rememberAccessibilityPreferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.steadymate.app.data.repository.OnboardingPreferencesRepository

// 🌃 Beautiful Dark Theme - Cozy, modern, and inspiring
private val BeautifulDarkColorScheme = darkColorScheme(
    // Primary - Beautiful purple theme
    primary = SteadyPurple,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = SteadyPurpleDark,
    onPrimaryContainer = SteadyPurpleLight,
    
    // Secondary - Joyful pink accents
    secondary = JoyfulPink,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = JoyfulPinkDark,
    onSecondaryContainer = JoyfulPinkLight,
    
    // Tertiary - Trust blue for reliability
    tertiary = TrustBlue,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = TrustBlueDark,
    onTertiaryContainer = TrustBlueLight,
    
    // Error - Vibrant but not harsh
    error = Color(0xFFFF6B6B),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF2D1B1B),
    onErrorContainer = Color(0xFFFFDADA),
    
    // Background - Rich dark with personality
    background = Color(0xFF0F0F23),      // Deep purple-tinted dark
    onBackground = Color(0xFFF8FAFC),    // Soft white
    surface = Color(0xFF1A1A2E),         // Elevated purple-dark
    onSurface = Color(0xFFF1F5F9),       // Clean white
    
    // Surface variants - Layered depths
    surfaceVariant = Color(0xFF16213E),   // Blue-tinted surface
    onSurfaceVariant = Color(0xFFCBD5E1), // Light blue-gray
    surfaceTint = SteadyPurple,
    
    // Outlines - Subtle but visible
    outline = Color(0xFF4A5568),
    outlineVariant = Color(0xFF2D3748),
    
    // Inverse colors
    inverseSurface = BeautifulLightColors.CloudWhite,
    inverseOnSurface = BeautifulLightColors.DarkText,
    inversePrimary = SteadyPurpleDark
)

// 🌅 Beautiful Light Theme - Clean, fresh, and delightful
private val BeautifulLightColorScheme = lightColorScheme(
    // Primary - Beautiful purple theme
    primary = SteadyPurpleDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = SteadyPurpleLight,
    onPrimaryContainer = SteadyPurpleDark,
    
    // Secondary - Joyful pink accents
    secondary = JoyfulPinkDark,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = JoyfulPinkLight,
    onSecondaryContainer = JoyfulPinkDark,
    
    // Tertiary - Trust blue for reliability
    tertiary = TrustBlueDark,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = TrustBlueLight,
    onTertiaryContainer = TrustBlueDark,
    
    // Error - Clear but not alarming
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    
    // Background - Pure, clean white
    background = BeautifulLightColors.PureWhite,
    onBackground = BeautifulLightColors.DarkText,
    surface = BeautifulLightColors.WarmWhite,
    onSurface = BeautifulLightColors.DarkText,
    
    // Surface variants - Subtle layers
    surfaceVariant = BeautifulLightColors.LightSurface1,
    onSurfaceVariant = BeautifulLightColors.MediumText,
    surfaceTint = SteadyPurple,
    
    // Outlines - Gentle definition
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    
    // Inverse colors
    inverseSurface = Color(0xFF1A1A2E),
    inverseOnSurface = Color(0xFFF1F5F9),
    inversePrimary = SteadyPurpleLight
)

// Legacy color schemes (keeping for compatibility)
private val DarkColorScheme = BeautifulDarkColorScheme
private val LightColorScheme = BeautifulLightColorScheme

// ⚡ ULTRA High Contrast Light Theme - Maximum accessibility with style
private val HighContrastLight = lightColorScheme(
    primary = EnhancedHighContrastColors.HCPrimary,
    onPrimary = EnhancedHighContrastColors.HCOnPrimary,
    primaryContainer = EnhancedHighContrastColors.HCPrimaryContainer,
    onPrimaryContainer = EnhancedHighContrastColors.HCOnPrimaryContainer,
    secondary = EnhancedHighContrastColors.HCSecondary,
    onSecondary = EnhancedHighContrastColors.HCOnSecondary,
    secondaryContainer = EnhancedHighContrastColors.HCSecondaryContainer,
    onSecondaryContainer = EnhancedHighContrastColors.HCOnSecondaryContainer,
    tertiary = EnhancedHighContrastColors.HCTertiary,
    onTertiary = EnhancedHighContrastColors.HCOnTertiary,
    tertiaryContainer = EnhancedHighContrastColors.HCTertiaryContainer,
    onTertiaryContainer = EnhancedHighContrastColors.HCOnTertiaryContainer,
    error = EnhancedHighContrastColors.HCError,
    onError = EnhancedHighContrastColors.HCOnError,
    errorContainer = EnhancedHighContrastColors.HCErrorContainer,
    onErrorContainer = EnhancedHighContrastColors.HCOnErrorContainer,
    background = EnhancedHighContrastColors.HCBackground,
    onBackground = EnhancedHighContrastColors.HCOnBackground,
    surface = EnhancedHighContrastColors.HCSurface,
    onSurface = EnhancedHighContrastColors.HCOnSurface,
    surfaceVariant = EnhancedHighContrastColors.HCSurfaceVariant,
    onSurfaceVariant = EnhancedHighContrastColors.HCOnSurfaceVariant,
    outline = EnhancedHighContrastColors.HCOutline,
    outlineVariant = EnhancedHighContrastColors.HCOutlineVariant
)

// ⚡ ULTRA High Contrast Dark Theme - Maximum visibility with personality
private val HighContrastDark = darkColorScheme(
    primary = EnhancedHighContrastColors.HCDarkPrimary,
    onPrimary = EnhancedHighContrastColors.HCDarkOnPrimary,
    primaryContainer = EnhancedHighContrastColors.HCDarkPrimaryContainer,
    onPrimaryContainer = EnhancedHighContrastColors.HCDarkOnPrimaryContainer,
    secondary = EnhancedHighContrastColors.HCDarkSecondary,
    onSecondary = EnhancedHighContrastColors.HCDarkOnSecondary,
    secondaryContainer = EnhancedHighContrastColors.HCDarkSecondaryContainer,
    onSecondaryContainer = EnhancedHighContrastColors.HCDarkOnSecondaryContainer,
    tertiary = EnhancedHighContrastColors.HCDarkTertiary,
    onTertiary = EnhancedHighContrastColors.HCDarkOnTertiary,
    tertiaryContainer = EnhancedHighContrastColors.HCDarkTertiaryContainer,
    onTertiaryContainer = EnhancedHighContrastColors.HCDarkOnTertiaryContainer,
    error = EnhancedHighContrastColors.HCDarkError,
    onError = EnhancedHighContrastColors.HCDarkOnError,
    errorContainer = EnhancedHighContrastColors.HCDarkErrorContainer,
    onErrorContainer = EnhancedHighContrastColors.HCDarkOnErrorContainer,
    background = EnhancedHighContrastColors.HCDarkBackground,
    onBackground = EnhancedHighContrastColors.HCDarkOnBackground,
    surface = EnhancedHighContrastColors.HCDarkSurface,
    onSurface = EnhancedHighContrastColors.HCDarkOnSurface,
    surfaceVariant = EnhancedHighContrastColors.HCDarkSurfaceVariant,
    onSurfaceVariant = EnhancedHighContrastColors.HCDarkOnSurfaceVariant,
    outline = EnhancedHighContrastColors.HCDarkOutline,
    outlineVariant = EnhancedHighContrastColors.HCDarkOutlineVariant
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
    colorPalette: String = "BEAUTIFUL", // BEAUTIFUL, MASCULINE, CLASSIC, HIGH_CONTRAST
    content: @Composable () -> Unit
) {
    val accessibility = rememberAccessibilityPreferences()

    val useDarkTheme = darkTheme || accessibility.isDarkMode
    val useDynamic = dynamicColor && accessibility.isDynamicColor
    val useHighContrast = (highContrast ?: accessibility.isHighContrast) || colorPalette == "HIGH_CONTRAST"

    val colorScheme = when {
        useHighContrast && useDarkTheme -> HighContrastDark
        useHighContrast && !useDarkTheme -> HighContrastLight
        colorPalette == "MASCULINE" && useDarkTheme -> MasculineDark
        colorPalette == "MASCULINE" && !useDarkTheme -> BeautifulLightColorScheme // Use light theme for masculine light mode
        useDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && colorPalette == "BEAUTIFUL" -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Classic uses the default Material 3 colors (Purple40/Purple80 based)
        colorPalette == "CLASSIC" && useDarkTheme -> darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )
        colorPalette == "CLASSIC" && !useDarkTheme -> lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40
        )
        useDarkTheme -> BeautifulDarkColorScheme
        else -> BeautifulLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            // Light status bar icons when using light theme (dark text/icons), false when dark theme.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
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

/**
 * SteadyMateAppTheme - A wrapper theme that automatically reads color palette from repository
 * This should be used at the app level to ensure consistent theming throughout the app
 */
@Composable
fun SteadyMateAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    largeFont: Boolean? = null,
    highContrast: Boolean? = null,
    reducedMotion: Boolean? = null,
    content: @Composable () -> Unit
) {
    // For now, default to BEAUTIFUL palette
    // TODO: Read from repository when available
    val colorPaletteString = "BEAUTIFUL"
    
    SteadyMateTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        largeFont = largeFont,
        highContrast = highContrast,
        reducedMotion = reducedMotion,
        colorPalette = colorPaletteString,
        content = content
    )
}
