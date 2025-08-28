# SteadyMate Onboarding System

## Overview

The SteadyMate onboarding system is a comprehensive 4-screen flow that guides new users through setting up their preferences, permissions, and profile. It uses Proto DataStore for efficient data persistence and state management.

## Architecture

### Core Components

1. **Proto DataStore**: `OnboardingPrefs.proto` defines the data structure
2. **Repository Pattern**: `OnboardingPreferencesRepository` handles data operations
3. **ViewModel**: `OnboardingViewModel` manages UI state and business logic
4. **Navigation**: `OnboardingNavigation.kt` handles screen transitions
5. **Screens**: Four dedicated composable screens for each step

### Data Structure

```protobuf
message OnboardingPrefs {
  Theme theme = 1;
  ConsentSettings consent_settings = 2;
  NotificationSettings notification_settings = 3;
  OnboardingStatus onboarding_status = 4;
}
```

## Onboarding Flow

### Screen 1: Welcome Screen
- **Purpose**: Introduction to SteadyMate
- **Content**: App features, benefits, and value proposition
- **Actions**: "Get Started" or "Skip Setup"
- **Data Stored**: Welcome completion flag

### Screen 2: Permissions & Privacy
- **Purpose**: User consent and permissions
- **Content**: Analytics, crash reporting, and notification permissions
- **Actions**: Enable/disable permissions, continue or skip
- **Data Stored**: Consent settings with timestamps

### Screen 3: Preferences
- **Purpose**: Theme and notification customization
- **Content**: 
  - Theme selection (Light/Dark/System)
  - Dynamic colors and high contrast options
  - Notification preferences
- **Actions**: Configure settings, continue or use defaults
- **Data Stored**: Theme and notification preferences

### Screen 4: Profile Setup
- **Purpose**: Personal information and goal setting
- **Content**: Display name and personal goal (optional)
- **Actions**: Complete setup or skip
- **Data Stored**: Profile completion flag and user preferences

## Implementation Details

### Dependencies

```kotlin
// Proto DataStore
implementation("androidx.datastore:datastore:1.0.0")
implementation("com.google.protobuf:protobuf-javalite:3.21.12")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
```

### Key Files

```
app/src/main/
├── proto/
│   └── OnboardingPrefs.proto
├── java/com/steadymate/app/
│   ├── data/
│   │   ├── datastore/
│   │   │   └── OnboardingPrefsSerializer.kt
│   │   └── repository/
│   │       ├── OnboardingPreferencesRepository.kt
│   │       └── OnboardingPreferencesRepositoryImpl.kt
│   ├── di/
│   │   ├── DataStoreModule.kt
│   │   └── RepositoryModule.kt
│   ├── navigation/
│   │   └── OnboardingNavigation.kt
│   └── ui/screens/onboarding/
│       ├── OnboardingViewModel.kt
│       ├── OnboardingWelcomeScreen.kt
│       ├── OnboardingPermissionsScreen.kt
│       ├── OnboardingPreferencesScreen.kt
│       └── OnboardingProfileSetupScreen.kt
```

### State Management

The system uses a combination of:
- **Proto DataStore**: Persistent storage for user preferences
- **StateFlow**: Reactive state updates in ViewModel
- **Compose State**: Local UI state management

```kotlin
val onboardingState = combine(
    onboardingRepository.getOnboardingPrefs(),
    _uiState
) { prefs, uiState ->
    OnboardingState(
        preferences = prefs,
        uiState = uiState
    )
}.stateIn(...)
```

### Data Persistence

All onboarding data is automatically persisted to Proto DataStore:

```kotlin
suspend fun updateTheme(theme: Theme) {
    onboardingPrefsDataStore.updateData { currentPrefs ->
        currentPrefs.toBuilder()
            .setTheme(theme)
            .build()
    }
}
```

## Features

### Progress Tracking
- ✅ Individual screen completion tracking
- ✅ Overall onboarding completion status
- ✅ Step progression with timestamps
- ✅ Ability to reset onboarding for testing

### Theme System
- ✅ Light/Dark/System theme modes
- ✅ Dynamic colors (Android 12+ Material You)
- ✅ High contrast mode for accessibility
- ✅ Custom accent color support

### Privacy & Consent
- ✅ Analytics consent with versioning
- ✅ Crash reporting permissions
- ✅ Data sharing preferences
- ✅ Consent timestamps for compliance

### Notifications
- ✅ Push notification toggles
- ✅ Granular notification categories
- ✅ Quiet hours configuration
- ✅ Custom reminder times

### Accessibility
- ✅ High contrast mode
- ✅ Proper semantic labels
- ✅ Screen reader support
- ✅ Large touch targets

## Usage

### Adding to Navigation Graph

```kotlin
// In your main NavHost
composable("main") {
    if (!isOnboardingCompleted) {
        onboardingNavigation(
            navController = navController,
            onOnboardingComplete = {
                // Navigate to main app
            }
        )
    } else {
        MainAppContent()
    }
}
```

### Checking Onboarding Status

```kotlin
@Composable
fun MainAppContent() {
    val onboardingRepository: OnboardingPreferencesRepository = hiltViewModel()
    val isCompleted by onboardingRepository
        .isOnboardingCompleted()
        .collectAsState(initial = false)
    
    if (!isCompleted) {
        OnboardingFlow()
    } else {
        AppContent()
    }
}
```

### Accessing User Preferences

```kotlin
@Composable
fun ThemedContent() {
    val onboardingRepository: OnboardingPreferencesRepository = hiltViewModel()
    val theme by onboardingRepository
        .getThemeSettings()
        .collectAsState(initial = Theme.getDefaultInstance())
    
    SteadyMateTheme(
        darkTheme = when (theme.mode) {
            Theme.ThemeMode.DARK -> true
            Theme.ThemeMode.LIGHT -> false
            else -> isSystemInDarkTheme()
        },
        dynamicColor = theme.useDynamicColors
    ) {
        // Your app content
    }
}
```

## Testing

### Unit Tests
- Repository functionality
- ViewModel state management
- Data serialization/deserialization

### UI Tests
- Screen navigation
- User interactions
- Data persistence

### Integration Tests
- End-to-end onboarding flow
- DataStore operations
- Theme application

## Future Enhancements

### Potential Features
- [ ] Onboarding analytics
- [ ] A/B testing for different flows
- [ ] Multi-language support
- [ ] Custom onboarding paths based on user type
- [ ] Progressive onboarding (in-app tutorials)
- [ ] Onboarding skip reasons tracking

### Performance Optimizations
- [ ] Lazy loading of heavy components
- [ ] Image optimization for illustrations
- [ ] Reduced navigation stack memory usage

## Best Practices

### Code Organization
- ✅ Separation of concerns (Repository pattern)
- ✅ Single responsibility principle
- ✅ Dependency injection with Hilt
- ✅ Reactive programming with Flow

### User Experience
- ✅ Clear progress indication
- ✅ Easy navigation (back button support)
- ✅ Skip options for each step
- ✅ Helpful descriptions and examples
- ✅ Non-blocking optional fields

### Data Management
- ✅ Efficient proto serialization
- ✅ Proper error handling
- ✅ Data validation
- ✅ Backward compatibility considerations

## Troubleshooting

### Common Issues

1. **Proto compilation errors**
   - Ensure protobuf plugin is properly configured
   - Check proto file syntax
   - Clean and rebuild project

2. **DataStore corruption**
   - Handle CorruptionException in serializer
   - Provide default values
   - Consider migration strategies

3. **Navigation issues**
   - Verify route definitions
   - Check ViewModel scoping
   - Ensure proper lifecycle management

### Debug Commands

```bash
# Build proto files
./gradlew generateProto

# Clean build
./gradlew clean build

# Run tests
./gradlew test connectedAndroidTest
```

## Contributing

When adding new onboarding features:

1. Update the proto definition if new data is needed
2. Extend repository interface and implementation
3. Add corresponding UI components
4. Update navigation graph
5. Write comprehensive tests
6. Update this documentation

---

This onboarding system provides a robust foundation for user onboarding while maintaining flexibility for future enhancements and customizations.
