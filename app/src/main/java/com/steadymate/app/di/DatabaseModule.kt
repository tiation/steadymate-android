package com.steadymate.app.di

import android.content.Context
import androidx.room.Room
import com.steadymate.app.data.database.SteadyDb
import com.steadymate.app.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database module for providing database-related dependencies.
 * This module is installed in SingletonComponent to ensure single instance across the app.
 * 
 * Provides:
 * - SteadyDb Room database instance
 * - All DAO instances for data access operations
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSteadyDatabase(
        @ApplicationContext context: Context
    ): SteadyDb {
        return Room.databaseBuilder(
            context,
            SteadyDb::class.java,
            SteadyDb.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // Remove in production
        .build()
    }
    
    @Provides
    fun provideMoodEntryDao(database: SteadyDb): MoodEntryDao {
        return database.moodEntryDao()
    }
    
    @Provides
    fun provideHabitDao(database: SteadyDb): HabitDao {
        return database.habitDao()
    }
    
    @Provides
    fun provideHabitTickDao(database: SteadyDb): HabitTickDao {
        return database.habitTickDao()
    }
}
