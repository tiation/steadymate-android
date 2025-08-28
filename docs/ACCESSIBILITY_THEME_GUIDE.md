# SteadyMate Accessibility Theme Guide

## Overview

The SteadyMate app now includes a comprehensive accessibility theme and interaction system that supports:

- 🎨 **Dynamic Colors** - Automatic color adaptation based on system theme
- 🌗 **Dark Mode** - Full dark theme support with proper contrast
- 🔍 **Large Font Support** - Typography scaling for visually impaired users
- 🔲 **High Contrast Mode** - Enhanced color contrast for better visibility  
- 🎭 **Reduced Motion** - Respectful animations for motion sensitivity
- 👆 **Large Touch Targets** - Enlarged interactive elements for motor accessibility
- 📢 **TalkBack Support** - Full screen reader compatibility with semantic helpers

## 🚀 Key Features Implemented

### 1. Enhanced SteadyMateTheme

The main theme function now supports all accessibility flags:

```kotlin
SteadyMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    largeFont: Boolean? = null,
    highContrast: Boolean? = null,
    reducedMotion: Boolean? = null,
    content: @Composable () -> Unit
)
```

**Features:**
- Automatically detects system accessibility settings
- Provides override controls for manual testing
- Applies appropriate color schemes based on preferences
- Scales typography for readability

### 2. Accessibility Preferences System

**Files:** `accessibility/AccessibilityPreferences.kt`

- Automatically reads system accessibility settings
- Provides manual overrides for testing
- Reactive state management with StateFlow
- Cross-platform compatibility

```kotlin
val preferences = rememberAccessibilityPreferences()
// Access: preferences.isLargeFont, preferences.isHighContrast, etc.
```

### 3. Touch Target Extensions

**Files:** `accessibility/TouchTargetModifiers.kt`

```kotlin
// Manual touch target sizing
Modifier.largeTouchTarget(enabled = true)

// Automatic sizing based on preferences  
Modifier.accessibleTouchTarget()

// Size scaling with different levels
Modifier.accessibleSize(baseSize = 48.dp)
```

**Features:**
- 48dp minimum (Material Design standard)
- 56dp large targets for accessibility
- 64dp extra-large for severe motor impairments
- Automatic scaling based on user preferences

### 4. Semantic Helpers for Screen Readers

**Files:** `accessibility/SemanticsHelpers.kt`

Comprehensive screen reader support including:

```kotlin
// Accessible descriptions
Modifier.accessibleDescription("Button label", Role.Button)

// Headings for screen readers
Modifier.accessibleHeading("Section title")

// Live regions for dynamic updates
Modifier.accessibleLiveRegion(LiveRegionMode.Polite)

// State descriptions for interactive elements
Modifier.accessibleState("Toggle is on")
```

**Pre-built Components:**
- `AccessibleActionButton` - Button with full accessibility
- `AccessibleCard` - Card with proper semantics
- `AccessibleErrorAnnouncement` - Error announcements
- `AccessibleStatusAnnouncement` - Status updates

### 5. Motion-Respectful Animations

**Files:** `accessibility/AnimationUtils.kt`

All animations automatically respect reduced motion preferences:

```kotlin
// Animations that adapt to user preferences
AccessibleAnimations.accessibleFadeIn()
AccessibleAnimations.accessibleSlideInLeft()
AccessibleAnimations.accessibleEnterTransition()
```

**Features:**
- Standard animations for normal users
- Simplified/faster animations for reduced motion users
- Consistent timing across the app
- Easy integration with existing Compose animations

### 6. High-Contrast Color Schemes  

**Files:** `Color.kt`

Dedicated high-contrast color palettes:

```kotlin
object HighContrastColors {
    // Pure black/white combinations
    val HCPrimary = Color(0xFF000000)    // Pure black
    val HCOnPrimary = Color(0xFFFFFFFF)  // Pure white
    // ... and more high-contrast color definitions
}
```

### 7. Scalable Typography System

**Files:** `Type.kt`

- `BaseTypography` - Standard Material 3 typography
- `LargeFontTypography` - 1.3x scaled typography for accessibility
- `getAccessibleTypography()` - Automatic typography selection

## 📱 How to Test

### Built APK Location
```
app/build/outputs/apk/debug/app-debug.apk
```

### System Settings to Test

1. **Large Font:**
   - Settings > Display > Font Size > Large/Largest

2. **High Contrast:**  
   - Settings > Accessibility > High Contrast Text

3. **Reduced Motion:**
   - Settings > Accessibility > Animation Scale = Off

4. **Dark Mode:**
   - Settings > Display > Dark Theme

5. **TalkBack (Screen Reader):**
   - Settings > Accessibility > TalkBack > On

### Manual Testing in App

The app includes a comprehensive demo section showing:
- Current accessibility settings
- Interactive components with proper semantics
- Touch target demonstrations
- Animation behavior changes
- Typography scaling examples

## 🛠 Usage Examples

### Basic Usage
```kotlin
@Composable
fun MyScreen() {
    SteadyMateTheme {
        // All accessibility features automatically applied
        MyContent()
    }
}
```

### Custom Button with Accessibility
```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier.accessibleButton(
        label = "Save document",
        onClick = { /* action */ }
    )
) {
    Text("Save")
}
```

### Accessible Navigation
```kotlin
LazyColumn {
    items(items) { item ->
        Card(
            modifier = Modifier.accessibleClickableOverlay(
                label = "Item: ${item.title}. ${item.description}",
                onClick = { navigate(item) }
            )
        ) {
            // Card content
        }
    }
}
```

### Status Updates
```kotlin
@Composable
fun StatusSection() {
    val status by viewModel.status.collectAsState()
    
    Text(
        text = "Status: $status",
        modifier = Modifier.accessibleLiveRegion()
    )
}
```

## 🎯 Best Practices

1. **Always use semantic helpers:**
   ```kotlin
   // Good ✅
   Text("Settings", modifier = Modifier.accessibleHeading())
   
   // Avoid ❌ 
   Text("Settings") // No semantic information
   ```

2. **Provide meaningful descriptions:**
   ```kotlin
   // Good ✅
   IconButton(
       onClick = { /* delete */ },
       modifier = Modifier.accessibleDescription("Delete item")
   ) {
       Icon(Icons.Default.Delete, contentDescription = null)
   }
   ```

3. **Use live regions for dynamic content:**
   ```kotlin
   // Good ✅ 
   Text(
       text = "Loading: $progress%",
       modifier = Modifier.accessibleLiveRegion()
   )
   ```

4. **Test with system accessibility settings enabled**

5. **Use the provided touch target extensions**

## 📚 File Structure

```
app/src/main/java/com/steadymate/app/ui/theme/
├── Theme.kt                    # Main theme with accessibility support
├── Color.kt                    # Colors including high-contrast palettes  
├── Type.kt                     # Typography with scaling support
└── accessibility/
    ├── AccessibilityPreferences.kt  # System integration & state
    ├── TouchTargetModifiers.kt      # Touch target extensions
    ├── SemanticsHelpers.kt          # Screen reader support
    ├── AnimationUtils.kt            # Motion-respectful animations
    └── AccessibilityExamples.kt     # Demonstration components
```

## 🔮 Future Enhancements

- Voice control integration
- Custom color blindness support
- Advanced haptic feedback
- Multi-language screen reader optimizations
- Focus management improvements

---

## ✅ Task Completion Summary

**Step 5: Design theme & accessibility layer** has been completed with:

- ✅ SteadyMateTheme supporting dynamic colours
- ✅ Full dark mode support  
- ✅ Large-font scaling system
- ✅ High-contrast color schemes
- ✅ Reduced-motion animation controls
- ✅ Modifier.largeTouchTarget() extension
- ✅ Comprehensive semantics helpers for TalkBack
- ✅ Working demonstration app with examples
- ✅ Successfully built APK ready for testing

The app now provides a world-class accessibility experience that adapts to user needs while maintaining beautiful design and smooth performance.
