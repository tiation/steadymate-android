# SteadyMate Navigation Architecture

## Overview
This document outlines the type-safe navigation implementation for the SteadyMate mental health app using Jetpack Compose Navigation with kotlinx.serialization.

## Architecture Components

### 1. SteadyRoute (Sealed Class)
**File:** `SteadyRoute.kt`
- Type-safe route definitions using `@Serializable` annotations
- Bottom navigation routes: Home, Tools, Habits, Insights, Settings
- Crisis overlay route (not in bottom navigation)
- Future nested routes with arguments (HabitDetail, ToolDetail, etc.)

### 2. BottomNavigationItem
**File:** `BottomNavigationItem.kt`
- Configuration for bottom navigation bar items
- Includes selected/unselected icons, titles, and badge support
- Uses Material Design icons for mental health context

### 3. ViewModels (HiltViewModel)
Each destination has its own ViewModel with proper Hilt injection:

- **HomeViewModel** - Already existed
- **ToolsViewModel** - Mental health tools and coping strategies
- **HabitsViewModel** - Daily habit tracking and completion
- **InsightsViewModel** - Analytics and progress tracking with time ranges
- **SettingsViewModel** - App preferences and configuration
- **CrisisViewModel** - Emergency resources and crisis support

### 4. Screen Composables
Each screen follows consistent architecture:
- Loading states with CircularProgressIndicator
- Error handling with user-friendly messages
- Content composables for main UI
- Proper ViewModel integration with `collectAsStateWithLifecycle`

### 5. Navigation Host
**File:** `SteadyMateNavigation.kt`
- Main navigation composable with Scaffold and bottom bar
- Type-safe NavHost implementation using the new Navigation Compose API
- Conditional bottom bar visibility (hidden for crisis overlay)
- Proper state management and back stack handling
- Extension function for crisis navigation from anywhere

### 6. MainActivity Integration
**File:** `MainActivity.kt`
- Clean, simplified implementation
- Full-screen SteadyMateNavigation integration
- Proper Hilt setup with @AndroidEntryPoint

## Key Features

### Type-Safe Navigation
- Using kotlinx.serialization for route arguments
- Compile-time safety for navigation parameters
- Easy to extend with new routes and arguments

### Bottom Navigation
- 5 main destinations: Home, Tools, Habits, Insights, Settings
- Proper state management with saveState/restoreState
- Badge support for notifications and news indicators
- Material Design 3 styling

### Crisis Support
- Special crisis overlay route accessible from anywhere
- Not part of bottom navigation for appropriate UX
- Quick access to mental health resources
- National crisis hotlines and emergency contacts

### State Management
- Each screen has its own ViewModel with proper lifecycle management
- Loading, error, and success states handled consistently
- Hilt dependency injection throughout the app

### Scalability
- Easy to add new destinations
- Type-safe argument passing for detail screens
- Modular architecture with clear separation of concerns
- Prepared for future features (habit details, tool details, etc.)

## Dependencies Added
- kotlinx-serialization plugin and runtime
- Proper gradle configuration for type-safe navigation

## Usage Examples

### Navigate to Crisis Screen
```kotlin
navController.navigateToCrisis()
```

### Navigate with Arguments (Future)
```kotlin
navController.navigate(SteadyRoute.HabitDetail(habitId = "habit123"))
```

### Access Route Arguments
```kotlin
val habitDetail = backStackEntry.toRoute<SteadyRoute.HabitDetail>()
val habitId = habitDetail.habitId
```

## Mental Health Context
The navigation is specifically designed for mental health support:
- Crisis support easily accessible
- Positive mental health tools and habits prioritized
- Insights for tracking mental wellness journey
- Settings for personalized mental health care preferences

This implementation provides a solid foundation for the SteadyMate app with proper architecture, type safety, and mental health-focused user experience.
