# SteadyMate Database Schema

This document describes the Room database implementation for the SteadyMate mental health tracking app.

## Database Overview

The `SteadyDb` Room database provides persistent storage for all user data in the SteadyMate app. It includes comprehensive mental health tracking features with proper relationships and indexing for optimal performance.

## Database Structure

### Entities

#### 1. UserEntity (`users` table)
- **Purpose**: User accounts and profile information
- **Key Fields**: id (PK), name, email, joinDate, streakCount, isActive
- **Features**: Simple user management with streak tracking

#### 2. MoodEntryEntity (`mood_entries` table)  
- **Purpose**: Daily mood tracking with emotional context
- **Key Fields**: id (PK), userId (FK), moodLevel (1-10), emotionTags, notes, timestamp, triggers, activities
- **Features**: Rich mood tracking with emotions, triggers, and activities
- **Indexes**: userId, timestamp, moodLevel for efficient querying

#### 3. HabitEntity (`habits` table)
- **Purpose**: User-defined habits to track
- **Key Fields**: id (PK), userId (FK), name, description, category, frequencyType, reminderTime, createdDate, isActive
- **Features**: Flexible habit definitions with reminders and categorization
- **Indexes**: userId, category, isActive, createdDate

#### 4. HabitTickEntity (`habit_ticks` table)
- **Purpose**: Individual habit completions/check-ins
- **Key Fields**: id (PK), habitId (FK), userId (FK), completedDate, completedAt, notes, mood, intensity, duration
- **Features**: Detailed completion tracking with optional metadata
- **Indexes**: habitId, userId, completedDate with unique constraint on (habitId, completedDate)

#### 5. ReframeEntryEntity (`reframe_entries` table)
- **Purpose**: Cognitive reframing exercises and outcomes
- **Key Fields**: id (PK), userId (FK), situation, automaticThought, emotions, reframedThought, emotionIntensity, newEmotionIntensity
- **Features**: Complete CBT-style thought challenging workflow
- **Indexes**: userId, timestamp, emotionIntensity

## Type Converters

The `Converters` class handles complex data types:

- **List<String>**: JSON serialization for emotion tags, triggers, activities, etc.
- **LocalDateTime**: ISO string format for timestamps
- **LocalDate**: ISO string format for dates
- **LocalTime**: ISO string format for reminder times
- **FrequencyType**: Enum handled natively by Room

## Data Access Objects (DAOs)

Each entity has a comprehensive DAO interface:

- **UserDao**: User CRUD operations, active user queries
- **MoodEntryDao**: Mood tracking with date ranges, averages, level filtering
- **HabitDao**: Habit management with category/frequency filtering  
- **HabitTickDao**: Completion tracking with streak calculations, statistics
- **ReframeEntryDao**: Reframing entries with improvement tracking

## Repository Interfaces

Domain-layer repository interfaces provide clean contracts:

- **MoodRepository**: Mood tracking operations
- **HabitRepository**: Habit management operations  
- **HabitTickRepository**: Habit completion tracking
- **ReframeRepository**: Cognitive reframing operations

## Key Features

### Relationships
- Foreign key constraints with CASCADE delete
- Proper indexing for query performance
- Unique constraints to prevent data duplication

### Performance Optimizations
- Strategic indexes on commonly queried columns
- Flow-based reactive data streams
- Batch operations for bulk inserts

### Mental Health Focus
- Comprehensive mood tracking with context
- Flexible habit system supporting various frequencies
- Evidence-based cognitive reframing workflows
- Privacy-focused local storage

### Data Integrity
- Foreign key constraints maintain referential integrity
- Unique constraints prevent duplicate entries
- Non-null constraints on critical fields

## Usage Examples

### Creating a Mood Entry
```kotlin
val moodEntry = MoodEntry(
    userId = "user123",
    moodLevel = 7,
    emotionTags = listOf("happy", "energetic", "focused"),
    notes = "Great workout this morning!",
    timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    activities = listOf("exercise", "work"),
    triggers = listOf("good_sleep")
)
```

### Creating a Habit
```kotlin
val habit = Habit(
    userId = "user123",
    name = "Morning Meditation",
    description = "10 minutes of mindfulness meditation",
    category = "wellness",
    targetFrequency = 1,
    frequencyType = FrequencyType.DAILY,
    reminderTime = LocalTime(7, 0),
    createdDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
)
```

### Recording a Habit Completion
```kotlin
val tick = HabitTick(
    habitId = habit.id,
    userId = "user123", 
    completedDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    completedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    notes = "Felt very peaceful today",
    mood = 8,
    duration = 12 // minutes
)
```

## Development Notes

- The database uses `fallbackToDestructiveMigration()` during development
- In production, proper migration strategies should be implemented
- All operations are suspend functions for coroutine support
- Flow-based queries provide reactive updates to the UI
- In-memory database factory available for testing

## File Structure

```
data/database/
├── SteadyDb.kt                 # Main database class
├── converters/
│   └── Converters.kt           # Type converters
├── entities/                   # Room entities
│   ├── UserEntity.kt
│   ├── MoodEntryEntity.kt  
│   ├── HabitEntity.kt
│   ├── HabitTickEntity.kt
│   └── ReframeEntryEntity.kt
└── dao/                        # Data Access Objects
    ├── UserDao.kt
    ├── MoodEntryDao.kt
    ├── HabitDao.kt  
    ├── HabitTickDao.kt
    └── ReframeEntryDao.kt
```

This implementation provides a solid foundation for a comprehensive mental health tracking application with Room database backing.
