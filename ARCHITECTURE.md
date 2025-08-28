# SteadyMate Architecture

This document outlines the multi-module architecture of the SteadyMate Android app, designed following Clean Architecture principles with proper separation of concerns.

## Project Structure

### Package-based Structure (Default)
```
app/src/main/java/com/steadymate/app/
├── data/               # Data layer implementation
│   ├── local/         # Room database, DataStore
│   ├── remote/        # API services, DTOs
│   └── repository/    # Repository implementations
├── domain/             # Business logic layer
│   ├── model/         # Domain entities
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Use cases/interactors
├── ui/                 # Presentation layer
│   ├── screens/       # Composable screens & ViewModels
│   ├── components/    # Reusable UI components
│   └── theme/         # Material Design theme
└── di/                 # Dependency injection modules
    ├── DatabaseModule.kt
    ├── NetworkModule.kt
    └── RepositoryModule.kt
```

### Multi-Module Structure (Optional)
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

## Architecture Layers

### 1. Domain Layer (`domain/` or `:core:domain`)
- **Purpose**: Contains business logic and domain entities
- **Dependencies**: No Android dependencies, pure Kotlin
- **Contents**:
  - Domain models (entities)
  - Repository interfaces
  - Use cases/interactors
  - Business rules

### 2. Data Layer (`data/` or `:core:data`)
- **Purpose**: Manages data from various sources
- **Dependencies**: Domain layer, Android frameworks
- **Contents**:
  - Repository implementations
  - Local data sources (Room, DataStore)
  - Remote data sources (Retrofit, API)
  - Data mappers/DTOs

### 3. Presentation Layer (`ui/` or feature modules)
- **Purpose**: UI components and presentation logic
- **Dependencies**: Domain layer, UI frameworks
- **Contents**:
  - Composable screens
  - ViewModels
  - UI state management
  - Navigation

### 4. DI Layer (`di/`)
- **Purpose**: Dependency injection configuration
- **Dependencies**: All layers
- **Contents**:
  - Hilt modules
  - Provider functions
  - Bindings

## Key Benefits

### Package-based Structure
- ✅ Simple to understand and navigate
- ✅ Easy IDE searchability with consistent naming
- ✅ Faster build times (single module)
- ✅ Good for small to medium teams

### Multi-Module Structure
- ✅ Better separation of concerns
- ✅ Parallel compilation (faster builds for large projects)
- ✅ Team scalability (different teams can work on different modules)
- ✅ Feature isolation and testability
- ✅ Gradle caching benefits

## Dependency Flow

```
app → feature modules → core modules

Features can depend on:
- :core:common (utilities, constants)
- :core:domain (business logic)
- :core:ui (shared components)

Core modules dependencies:
- :core:data → :core:domain, :core:common
- :core:domain → :core:common
- :core:ui → :core:common
```

## Hilt Integration

All modules are wired through Hilt for dependency injection:

```kotlin
// Repository binding
@Binds
@Singleton
abstract fun bindUserRepository(
    userRepositoryImpl: UserRepositoryImpl
): UserRepository

// Use case injection
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel()
```

## Naming Conventions

### Package Names
- `com.steadymate.app.*` - App module
- `com.steadymate.core.*` - Core modules
- `com.steadymate.feature.*` - Feature modules

### Class Names
- **Entities**: `User`, `Habit`, `CheckIn`
- **Repositories**: `UserRepository` (interface), `UserRepositoryImpl` (implementation)
- **Use Cases**: `GetCurrentUserUseCase`, `SaveUserUseCase`
- **ViewModels**: `HomeViewModel`, `CheckInViewModel`
- **Screens**: `HomeScreen`, `CheckInScreen`

### File Organization
- Group related files in subpackages
- Use descriptive names for easy IDE searching
- Maintain consistent structure across modules

## Testing Strategy

- **Unit Tests**: Domain layer (use cases, business logic)
- **Integration Tests**: Data layer (repositories, database)
- **UI Tests**: Presentation layer (Composable screens)
- **End-to-End Tests**: App module (user flows)

## Migration Path

To switch from package-based to multi-module:
1. Create core modules structure
2. Move domain entities to `:core:domain`
3. Move data implementations to `:core:data`
4. Create feature modules for major app sections
5. Update dependency injection
6. Test thoroughly before production

## Best Practices

1. **Keep modules focused**: Each module should have a single responsibility
2. **Minimize dependencies**: Reduce coupling between modules
3. **Use interfaces**: Define contracts in domain layer
4. **Consistent naming**: Follow established conventions for IDE searchability
5. **Documentation**: Keep architecture documentation updated
