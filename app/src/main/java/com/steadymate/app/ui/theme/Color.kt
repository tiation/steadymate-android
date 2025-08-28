package com.steadymate.app.ui.theme

import androidx.compose.ui.graphics.Color

// Base Material 3 colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// SteadyMate brand colors
val SteadyBlue = Color(0xFF2E7BE9)
val SteadyBlueLight = Color(0xFF6BB6FF)
val SteadyBlueDark = Color(0xFF004BA0)

val SteadyGreen = Color(0xFF34A853)
val SteadyGreenLight = Color(0xFF8BC34A)
val SteadyGreenDark = Color(0xFF1B5E20)

val SteadyOrange = Color(0xFFFF9800)
val SteadyOrangeLight = Color(0xFFFFC107)
val SteadyOrangeDark = Color(0xFFE65100)

// High contrast colors for accessibility
object HighContrastColors {
    // High contrast light theme
    val HCPrimary = Color(0xFF000000)           // Pure black
    val HCOnPrimary = Color(0xFFFFFFFF)         // Pure white
    val HCPrimaryContainer = Color(0xFF1A1A1A)  // Dark gray
    val HCOnPrimaryContainer = Color(0xFFFFFFFF)
    
    val HCSecondary = Color(0xFF0000FF)         // Pure blue
    val HCOnSecondary = Color(0xFFFFFFFF)
    val HCSecondaryContainer = Color(0xFFE3F2FD)
    val HCOnSecondaryContainer = Color(0xFF000000)
    
    val HCTertiary = Color(0xFF7F0000)          // Dark red
    val HCOnTertiary = Color(0xFFFFFFFF)
    val HCTertiaryContainer = Color(0xFFFDE7E7)
    val HCOnTertiaryContainer = Color(0xFF000000)
    
    val HCError = Color(0xFFD32F2F)             // High contrast red
    val HCOnError = Color(0xFFFFFFFF)
    val HCErrorContainer = Color(0xFFF8D7DA)
    val HCOnErrorContainer = Color(0xFF000000)
    
    val HCBackground = Color(0xFFFFFFFF)        // Pure white
    val HCOnBackground = Color(0xFF000000)      // Pure black
    val HCSurface = Color(0xFFFFFFFF)
    val HCOnSurface = Color(0xFF000000)
    
    val HCSurfaceVariant = Color(0xFFF5F5F5)    // Light gray
    val HCOnSurfaceVariant = Color(0xFF000000)
    val HCOutline = Color(0xFF000000)           // Pure black outline
    val HCOutlineVariant = Color(0xFF333333)    // Dark gray outline
    
    // High contrast dark theme
    val HCDarkPrimary = Color(0xFFFFFFFF)       // Pure white
    val HCDarkOnPrimary = Color(0xFF000000)     // Pure black
    val HCDarkPrimaryContainer = Color(0xFFE0E0E0)
    val HCDarkOnPrimaryContainer = Color(0xFF000000)
    
    val HCDarkSecondary = Color(0xFF00FFFF)     // Cyan
    val HCDarkOnSecondary = Color(0xFF000000)
    val HCDarkSecondaryContainer = Color(0xFF1A1A1A)
    val HCDarkOnSecondaryContainer = Color(0xFFFFFFFF)
    
    val HCDarkTertiary = Color(0xFFFF6B6B)      // Light red
    val HCDarkOnTertiary = Color(0xFF000000)
    val HCDarkTertiaryContainer = Color(0xFF1A1A1A)
    val HCDarkOnTertiaryContainer = Color(0xFFFFFFFF)
    
    val HCDarkError = Color(0xFFFF5252)         // Bright red
    val HCDarkOnError = Color(0xFF000000)
    val HCDarkErrorContainer = Color(0xFF1A1A1A)
    val HCDarkOnErrorContainer = Color(0xFFFFFFFF)
    
    val HCDarkBackground = Color(0xFF000000)    // Pure black
    val HCDarkOnBackground = Color(0xFFFFFFFF)  // Pure white
    val HCDarkSurface = Color(0xFF000000)
    val HCDarkOnSurface = Color(0xFFFFFFFF)
    
    val HCDarkSurfaceVariant = Color(0xFF1A1A1A)
    val HCDarkOnSurfaceVariant = Color(0xFFFFFFFF)
    val HCDarkOutline = Color(0xFFFFFFFF)       // Pure white outline
    val HCDarkOutlineVariant = Color(0xFFCCCCCC) // Light gray outline
}

// Masculine Dark Mode Color Palette
object MasculineDarkColors {
    // Core brand colors - Strong and bold
    val DeepCharcoal = Color(0xFF1A1D23)      // Main background - industrial dark
    val StormGray = Color(0xFF2D3142)         // Surface - steel-like
    val IronGray = Color(0xFF4F5D75)          // Secondary surface
    val SteelBlue = Color(0xFF1E3A5F)         // Primary container
    
    // Primary colors - Electric blue with authority
    val ElectricBlue = Color(0xFF00D4FF)      // Primary - electric, confident
    val CyberBlue = Color(0xFF0099CC)         // Primary variant
    val DeepElectric = Color(0xFF005577)      // Primary dark
    
    // Accent colors - Bold and commanding
    val NeonGreen = Color(0xFF39FF14)         // Success/positive - high energy
    val BurntOrange = Color(0xFFFF6B35)       // Warning - industrial orange
    val CrimsonRed = Color(0xFFFF073A)        // Error - powerful red
    val AmberYellow = Color(0xFFFFBF00)       // Info - golden amber
    
    // Text colors - High contrast and readable
    val PureWhite = Color(0xFFFFFFFF)         // Primary text
    val SilverGray = Color(0xFFBDBDBD)        // Secondary text
    val DimGray = Color(0xFF757575)           // Tertiary text
    val CarbonBlack = Color(0xFF121212)       // On light surfaces
    
    // Surface variations
    val MidnightBlack = Color(0xFF0F1113)     // Darkest surface
    val SlateGray = Color(0xFF353A42)         // Elevated surface
    val GraphiteGray = Color(0xFF454B54)      // Card backgrounds
    
    // Status and feedback colors
    val MatrixGreen = Color(0xFF00FF41)       // Success - matrix green
    val VolcanicOrange = Color(0xFFFF4500)    // Warning - lava orange
    val LaserRed = Color(0xFFFF1744)          // Danger - laser red
    val ArcticBlue = Color(0xFF00FFFF)        // Info - cold blue
    
    // Container colors for status
    val GreenContainer = Color(0xFF1A2F1A)    // Dark green container
    val OrangeContainer = Color(0xFF2F1F0A)   // Dark orange container
    val RedContainer = Color(0xFF2F1515)      // Dark red container
    val BlueContainer = Color(0xFF0A1F2F)     // Dark blue container
}

// Status colors with accessibility considerations
object StatusColors {
    val Success = Color(0xFF2E7D32)      // High contrast green
    val Warning = Color(0xFFF57C00)      // High contrast orange
    val Error = Color(0xFFD32F2F)        // High contrast red
    val Info = Color(0xFF1976D2)         // High contrast blue
    
    // Light variants
    val SuccessContainer = Color(0xFFC8E6C9)
    val WarningContainer = Color(0xFFFFE0B2)
    val ErrorContainer = Color(0xFFFFEBEE)
    val InfoContainer = Color(0xFFE3F2FD)
    
    // Dark variants
    val SuccessContainerDark = Color(0xFF1B5E20)
    val WarningContainerDark = Color(0xFFE65100)
    val ErrorContainerDark = Color(0xFFB71C1C)
    val InfoContainerDark = Color(0xFF0D47A1)
}
