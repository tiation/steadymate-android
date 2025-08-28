package com.steadymate.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.steadymate.app.data.database.converters.LocalDateTimeConverter
import com.steadymate.app.data.database.converters.StringListConverter
import com.steadymate.app.data.database.dao.*
import com.steadymate.app.data.database.entities.*

/**
 * Room database for the SteadyMate app.
 * Contains all entities and provides access to DAOs for data operations.
 * 
 * Database schema includes:
 * - MoodEntries: Simple mood check-ins (0-10 scale with tags and optional note)
 * - Habits: Binary habits with schedule and optional reminders
 * - HabitTicks: Habit completion tracking by date
 */
@Database(
    entities = [
        MoodEntryEntity::class,
        HabitEntity::class,
        HabitTickEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class, StringListConverter::class)
abstract class SteadyDb : RoomDatabase() {
    
    // DAO access methods
    abstract fun moodEntryDao(): MoodEntryDao
    abstract fun habitDao(): HabitDao
    abstract fun habitTickDao(): HabitTickDao
    
    companion object {
        const val DATABASE_NAME = "steady_database"
        
        @Volatile
        private var INSTANCE: SteadyDb? = null
        
        /**
         * Gets the singleton database instance.
         * Creates the database if it doesn't exist.
         */
        fun getDatabase(context: Context): SteadyDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SteadyDb::class.java,
                    DATABASE_NAME
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Gets the singleton database instance with fallback to destructive migration.
         * Use this during development when schema changes are frequent.
         */
        fun getDatabaseWithFallback(context: Context): SteadyDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SteadyDb::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration() // Only use during development
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Creates an in-memory database for testing.
         * Data is lost when the process is killed.
         */
        fun getInMemoryDatabase(context: Context): SteadyDb {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                SteadyDb::class.java
            ).build()
        }
    }
}
