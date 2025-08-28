# DI Baseline Usage Guide

## Overview
This document explains how to use the DI (Dependency Injection) baseline implemented for the SteadyMate Android app using Hilt.

## Components Implemented

### 1. SteadyMateApplication
- **Location**: `app/src/main/java/com/steadymate/app/SteadyMateApplication.kt`
- **Annotation**: `@HiltAndroidApp`
- **Features**:
  - Triggers Hilt's code generation
  - Implements `Configuration.Provider` for WorkManager
  - Injects custom `WorkerFactory`

### 2. AppModule
- **Location**: `app/src/main/java/com/steadymate/app/di/AppModule.kt`
- **Provides**:
  - CoroutineDispatcher qualifiers
  - DataStore for preferences
  - WorkManager configuration
  - Room database (ready for implementation)
  - DAO providers (ready for implementation)

### 3. CoroutineDispatcher Qualifiers
Four qualifier annotations for different threading scenarios:

#### @DispatcherMain
```kotlin
@Inject
@DispatcherMain
lateinit var mainDispatcher: CoroutineDispatcher
```
**Use for**: UI operations, main thread work

#### @DispatcherIO
```kotlin
@Inject
@DispatcherIO
lateinit var ioDispatcher: CoroutineDispatcher
```
**Use for**: Network requests, file I/O, database operations

#### @DispatcherDefault
```kotlin
@Inject
@DispatcherDefault
lateinit var defaultDispatcher: CoroutineDispatcher
```
**Use for**: CPU-intensive tasks like JSON parsing, sorting, filtering

#### @DispatcherUnconfined
```kotlin
@Inject
@DispatcherUnconfined
lateinit var unconfinedDispatcher: CoroutineDispatcher
```
**Use for**: Testing or specialized use cases (use sparingly)

## Usage Examples

### In Repository Classes
```kotlin
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    // private val userDao: UserDao, // When Room is implemented
    // private val userApiService: UserApiService // When API is implemented
) : UserRepository {

    override suspend fun saveUser(user: User) = withContext(ioDispatcher) {
        // Database operations run on IO dispatcher
        // userDao.insertUser(user.toEntity())
    }
}
```

### In ViewModels
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetCurrentUserUseCase,
    @DispatcherMain private val mainDispatcher: CoroutineDispatcher,
    @DispatcherDefault private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun processData() {
        viewModelScope.launch(defaultDispatcher) {
            // CPU-intensive processing
            val processedData = heavyComputation()
            
            withContext(mainDispatcher) {
                // Update UI state on main thread
                _uiState.value = _uiState.value.copy(data = processedData)
            }
        }
    }
}
```

### In Worker Classes (When Implemented)
```kotlin
@AssistedFactory
interface HabitReminderWorkerFactory {
    fun create(@Assisted context: Context, @Assisted params: WorkerParameters): HabitReminderWorker
}

@HiltWorker
class HabitReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitRepository: HabitRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            // Perform work on IO dispatcher
            habitRepository.sendReminders()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
```

## DataStore Usage

### Accessing DataStore
```kotlin
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val THEME_MODE = intPreferencesKey("theme_mode")
    }
    
    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }
    
    fun getUserName(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_NAME]
        }
    }
}
```

## Next Steps

### When Adding Room Database
1. Create entity classes in `app/src/main/java/com/steadymate/app/data/local/entity/`
2. Create DAO interfaces in `app/src/main/java/com/steadymate/app/data/local/dao/`
3. Create database class implementing `RoomDatabase`
4. Uncomment the database providers in `AppModule.kt`

### When Adding Custom Workers
1. Create worker classes with `@HiltWorker` annotation
2. Use `@AssistedInject` in constructor
3. Create `@AssistedFactory` interfaces
4. Update `WorkerFactory` in `AppModule.kt` to use `HiltWorkerFactory`

## Configuration

### AndroidManifest.xml
The manifest has been updated to reference `SteadyMateApplication`:
```xml
<application
    android:name=".SteadyMateApplication"
    ...>
```

### Activity Setup
Activities should be annotated with `@AndroidEntryPoint`:
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Activity implementation
}
```

## Testing

### Unit Tests
For unit tests, you can provide test dispatchers:
```kotlin
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryImplTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var repository: UserRepositoryImpl
    
    @BeforeEach
    fun setup() {
        repository = UserRepositoryImpl(
            dataStore = mockDataStore,
            ioDispatcher = testDispatcher
        )
    }
}
```

This DI baseline provides a solid foundation for dependency injection throughout the SteadyMate app, with proper separation of concerns and threading management.
