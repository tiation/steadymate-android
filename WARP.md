# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

# ⛵️ WARP – SteadyMate Android

A modern Android mental health app built with Clean Architecture, Jetpack Compose, and comprehensive accessibility support.

## 📋 Table of Contents

1. [Essential Build & CLI Commands](#1-essential-build--cli-commands)
2. [Multi-Module Clean Architecture](#2-multi-module-clean-architecture)
3. [Development Workflows](#3-development-workflows)
4. [Technology Stack](#4-technology-stack)
5. [Project-Specific Systems](#5-project-specific-systems)
6. [Working with Proto DataStore](#6-working-with-proto-datastore)
7. [Module Dependencies & Build Config](#7-module-dependencies--build-config)
8. [Testing Strategy](#8-testing-strategy)
9. [Dev Environment Setup](#9-dev-environment-setup)
10. [Architecture & Best Practices](#10-architecture--best-practices)

---

## 1. Essential Build & CLI Commands

### Core Gradle Tasks

```bash
# Clean and build entire project
./gradlew clean build

# Build debug/release APKs
./gradlew assembleDebug
./gradlew assembleRelease

# Lint checking
./gradlew lint

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Check dependencies
./gradlew dependencies

# Generate protobuf files (for DataStore)
./gradlew generateProto

# Build with performance insights
./gradlew build --scan
```

### Useful Gradle Flags

```bash
# Speed up builds
./gradlew build --configuration-cache
./gradlew build --no-daemon
./gradlew build --parallel

# Force refresh dependencies
./gradlew build --refresh-dependencies
```

### Warp Workflows (Copy to .warp/workflows.yaml)

```yaml
build_debug:
  name: "🔨 Build Debug APK"
  command: "./gradlew assembleDebug"
  
run_tests:
  name: "🧪 Run All Tests"  
  command: "./gradlew test"
  
lint_check:
  name: "🔍 Lint Check"
  command: "./gradlew lint"
  
clean_build:
  name: "🧹 Clean Build"
  command: "./gradlew clean build"
```

## 2. Multi-Module Clean Architecture

### Module Structure

```
SteadyMate/
├── app/                    # Main application module
├── core/                   # Shared core modules
│   ├── common/            # Common utilities, constants
│   ├── data/              # Data layer (repositories, local/remote)
│   ├── domain/            # Business logic (entities, use cases)
│   └── ui/                # Shared UI components, theme
└── feature/               # Feature-specific modules
    ├── home/              # Home screen feature
    └── checkin/           # Check-in feature
```

### Dependency Flow

```
app → feature modules → core modules

Allowed dependencies:
• Features can depend on: :core:common, :core:domain, :core:ui
• :core:data → :core:domain, :core:common
• :core:domain → :core:common
• :core:ui → :core:common
```

### Module Purposes

- **app**: Main application, navigation host, dependency injection
- **core:common**: Utilities, constants, extension functions
- **core:data**: Repository implementations, local/remote data sources
- **core:domain**: Business entities, repository interfaces, use cases
- **core:ui**: Shared composables, theme system, UI components
- **feature:home**: Home screen implementation
- **feature:checkin**: Mental health check-in functionality

## 3. Development Workflows

### Build Variants

```bash
# Debug builds (default)
./gradlew assembleDebug

# Release builds
./gradlew assembleRelease
```

### Testing Workflows

```bash
# Run unit tests for all modules
./gradlew testDebugUnitTest

# Run UI tests (requires device)
./gradlew connectedDebugAndroidTest

# Run specific module tests
./gradlew :app:testDebugUnitTest
./gradlew :core:domain:test
```

### Debugging

```bash
# View logcat in terminal
adb logcat | grep "SteadyMate"

# Install debug APK
./gradlew installDebug

# Android Studio debugging
# Open project in AS, set breakpoints, run/debug configuration
```

### Performance & Caching

The project is configured for optimal build performance:
- Configuration cache enabled (`org.gradle.configuration-cache=true`)
- Build cache enabled (`org.gradle.caching=true`)  
- Resource optimizations enabled
- Non-transitive R classes for faster builds

## 4. Technology Stack

### UI & Presentation
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Latest Material Design system with dynamic theming
- **Compose BOM 2024.02.00**: Manages all Compose library versions
- **Navigation Compose**: Type-safe navigation with kotlinx.serialization

### Architecture & DI
- **Hilt**: Dependency injection framework
- **Clean Architecture**: Separation of concerns with domain/data/UI layers
- **Repository Pattern**: Abstraction over data sources
- **MVVM**: ViewModels manage UI state

### Data & Persistence  
- **Proto DataStore**: Type-safe preference storage
- **Room**: Local database (ready for implementation)
- **Kotlin Coroutines & Flow**: Reactive async programming

### Additional Libraries
- **WorkManager**: Background task scheduling
- **Vico Charts**: Data visualization for mental health insights
- **Accompanist**: Compose utility libraries (SystemUI, Permissions)
- **Kotlinx DateTime**: Date/time handling
- **Kotlinx Serialization**: Type-safe serialization

## 5. Project-Specific Systems

### Onboarding System
- **Proto DataStore**: `OnboardingPrefs.proto` for user preferences
- **4-Screen Flow**: Welcome → Permissions → Preferences → Profile
- **State Management**: Repository pattern with reactive Flow
- **Documentation**: See `docs/ONBOARDING_SYSTEM.md`

### Accessibility Layer
- **Dynamic Colors**: Material You support (Android 12+)
- **Theme System**: Light/Dark/System modes with high contrast
- **Large Font Support**: Typography scaling for accessibility
- **Touch Targets**: Enlarged interactive elements (48dp+)
- **Screen Reader**: Full TalkBack support with semantic helpers
- **Motion Sensitivity**: Reduced animation support
- **Documentation**: See `docs/ACCESSIBILITY_THEME_GUIDE.md`

### Type-Safe Navigation
- **Sealed Routes**: `@Serializable` route definitions
- **Bottom Navigation**: 5 main destinations (Home, Tools, Habits, Insights, Settings)
- **Crisis Support**: Dedicated crisis overlay route
- **Documentation**: See `navigation/NAVIGATION_OVERVIEW.md`

### Dependency Injection Baseline
- **Coroutine Dispatchers**: `@DispatcherMain`, `@DispatcherIO`, `@DispatcherDefault`
- **DataStore Integration**: Pre-configured Proto DataStore
- **WorkManager**: Hilt integration for background tasks
- **Documentation**: See `docs/DI_BASELINE_USAGE.md`

## 6. Working with Proto DataStore

### Protobuf Schema

The project uses Protocol Buffers for type-safe preferences storage:

```proto
// app/src/main/proto/OnboardingPrefs.proto
message OnboardingPrefs {
  Theme theme = 1;
  ConsentSettings consent_settings = 2;
  NotificationSettings notification_settings = 3;
  OnboardingStatus onboarding_status = 4;
}
```

### Compilation & Usage

```bash
# Generate protobuf classes
./gradlew generateProto

# Classes generated at:
# app/build/generated/source/proto/main/javalite/
```

### Repository Pattern

```kotlin
@Singleton
class OnboardingPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<OnboardingPrefs>
) : OnboardingPreferencesRepository {
    
    override fun getTheme(): Flow<Theme> {
        return dataStore.data.map { it.theme }
    }
    
    override suspend fun updateTheme(theme: Theme) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder().setTheme(theme).build()
        }
    }
}
```

### Schema Evolution

When updating the proto schema:
1. Add new fields with unique field numbers
2. Mark deprecated fields as `[deprecated = true]`  
3. Run `./gradlew generateProto` to update generated classes
4. Update serializer if needed

## 7. Module Dependencies & Build Config

### Gradle Configuration
- **AGP**: 8.2.2 (Android Gradle Plugin)
- **Kotlin**: 1.9.22
- **Gradle Wrapper**: 8.7
- **Compile SDK**: 34
- **Target SDK**: 34  
- **Min SDK**: 24 (Android 7.0+)

### Key Plugin Versions
- **Hilt**: Managed via Compose BOM
- **Protobuf**: Auto-configured
- **Kotlin Serialization**: Auto-configured

### Dependency Management

Dependencies are managed in `app/build.gradle.kts` using hardcoded versions. Key patterns:

```kotlin
// Compose BOM manages all Compose versions
val composeBom = platform("androidx.compose:compose-bom:2024.02.00")

// Core dependencies
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Hilt DI
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")
```

### Adding New Dependencies

1. Add to appropriate build.gradle.kts (app, core module, or feature module)
2. Follow version patterns established in existing dependencies
3. Add to appropriate module based on dependency rules
4. Sync and verify build

## 8. Testing Strategy

### Test Architecture

The project uses a three-layer testing approach:

#### Unit Tests
- **Framework**: JUnit 5 Jupiter
- **Mocking**: MockK
- **Flow Testing**: Turbine
- **Coroutines**: `kotlinx-coroutines-test`
- **Scope**: Domain layer, ViewModels, Repository interfaces

```bash
# Run all unit tests
./gradlew test

# Run specific module unit tests
./gradlew :core:domain:test
./gradlew :app:testDebugUnitTest
```

#### Integration Tests
- **Database**: Room testing utilities
- **DataStore**: In-memory DataStore for tests  
- **Repository**: Test implementations against real data sources

#### UI Tests
- **Framework**: Compose UI Test, Espresso
- **Device Tests**: `androidTestImplementation` dependencies
- **End-to-End**: User workflow testing

```bash
# Run instrumented tests (device required)
./gradlew connectedDebugAndroidTest

# Run UI tests only
./gradlew :app:connectedDebugAndroidTest
```

### Test Utilities

```kotlin
// Main dispatcher rule for ViewModel tests
@get:Rule
val mainDispatcherRule = MainDispatcherRule()

// Test dispatchers for repositories
private val testDispatcher = UnconfinedTestDispatcher()

// DataStore testing
private val testDataStore = DataStoreFactory.create(
    serializer = OnboardingPrefsSerializer,
    scope = TestScope(testDispatcher)
)
```

### Testing Commands Cheat Sheet

```bash
./gradlew test                    # All unit tests
./gradlew connectedAndroidTest   # All instrumented tests  
./gradlew testDebugUnitTest      # Debug unit tests only
./gradlew :app:test              # App module tests only
./gradlew --continue test        # Continue on test failures
```

## 9. Dev Environment Setup

### Required Tools

- **Android Studio**: Hedgehog | 2023.1.1 or later
- **JDK**: 8 or 17 (managed by Android Studio)
- **Android SDK**: API level 34
- **Gradle**: 8.7 (via wrapper, don't install globally)

### Optional Tools

- **Warp Terminal**: Enhanced terminal experience
- **Git Hooks**: Pre-commit linting and testing
- **ktlint**: Kotlin code formatting (future addition)

### Quick Start

```bash
# Clone repository
git clone <repository-url>
cd steadymate

# Verify Gradle wrapper
./gradlew --version

# Open in Android Studio
# File → Open → Select project root folder

# Build project
./gradlew build

# Run on device/emulator
./gradlew installDebug
```

### IDE Configuration

1. **Android Studio**: Import project, let Gradle sync
2. **Compose Preview**: Enable in Build → Compose → Preview
3. **Logcat Filtering**: Use "SteadyMate" tag for app logs
4. **Device Manager**: Set up AVD for API 34+ testing

### Troubleshooting

```bash
# Clean and rebuild on issues
./gradlew clean build

# Refresh dependencies
./gradlew build --refresh-dependencies

# Check Gradle configuration
./gradlew projects
```

## 10. Architecture & Best Practices

### Clean Architecture Rules

1. **Domain Layer**: Pure Kotlin, no Android dependencies
   - Entities, use cases, repository interfaces
   - Business logic and rules

2. **Data Layer**: Implements domain contracts
   - Repository implementations
   - Local (Room, DataStore) and remote data sources
   - Data mappers and DTOs

3. **Presentation Layer**: UI and Android framework
   - Composable screens and ViewModels
   - Navigation and UI state management

### Development Guidelines

#### Single Activity Architecture
- One `MainActivity` with Compose navigation
- Screen-level composables for major destinations
- Bottom navigation for main app flow

#### Dependency Injection with Hilt
- `@HiltAndroidApp` application class
- `@HiltViewModel` for ViewModels
- Module-based DI configuration
- Repository interface bindings

#### State Management
- Immutable data classes for UI state
- `StateFlow` for reactive state updates
- `collectAsStateWithLifecycle` in composables
- Single source of truth principle

#### Accessibility First Design
- Material Design 3 with dynamic theming
- Semantic descriptions for screen readers
- Large touch targets (48dp minimum)
- High contrast mode support
- Reduced motion preferences

### Adding New Features

1. **New Module**: Add to `settings.gradle.kts` and create `build.gradle.kts`
2. **Domain First**: Define entities and use cases in `:core:domain`
3. **Repository**: Interface in domain, implementation in `:core:data`
4. **UI Components**: Feature-specific UI in feature modules
5. **Navigation**: Update route definitions and navigation graph
6. **DI**: Add Hilt modules for new dependencies
7. **Tests**: Unit tests for domain, integration tests for data, UI tests for features

### Code Organization

```kotlin
// Package structure example
com.steadymate.app/
├── data/               # Data implementations
├── domain/             # Business logic
├── ui/                 # UI components and screens  
├── navigation/         # Navigation configuration
└── di/                 # Dependency injection modules
```

### Performance Considerations

- Use `LazyColumn`/`LazyRow` for lists
- Implement proper Compose key functions
- Avoid unnecessary recompositions
- Use `remember` and `rememberSaveable` appropriately
- Profile with Android Studio profiler

---

## 🚀 Getting Started

The fastest way to start contributing:

1. Clone and open in Android Studio
2. Run `./gradlew build` to verify setup
3. Check out `docs/` folder for detailed system documentation
4. Review `ARCHITECTURE.md` for architectural decisions
5. Run `./gradlew test` to ensure tests pass

For questions about specific systems, refer to the detailed documentation in the `docs/` folder.
