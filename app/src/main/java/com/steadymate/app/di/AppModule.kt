package com.steadymate.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.work.WorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Main application module providing core dependencies.
 * 
 * This module provides:
 * - CoroutineDispatcher qualifiers for different thread pools
 * - Room database instance (when implemented)
 * - DAO providers (when entities are implemented)
 * - DataStore for preferences
 * - WorkManager configuration
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // DataStore extension property
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "steadymate_preferences")

    /**
     * Provides DataStore instance for storing user preferences.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * Provides Main/UI CoroutineDispatcher.
     */
    @Provides
    @DispatcherMain
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    /**
     * Provides IO CoroutineDispatcher for network and file operations.
     */
    @Provides
    @DispatcherIO
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides Default CoroutineDispatcher for CPU-intensive tasks.
     */
    @Provides
    @DispatcherDefault
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides Unconfined CoroutineDispatcher for testing or specialized use cases.
     */
    @Provides
    @DispatcherUnconfined
    fun provideUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    /**
     * Provides WorkerFactory for custom Worker dependency injection.
     */
    @Provides
    @Singleton
    fun provideWorkerFactory(): WorkerFactory {
        // TODO: Replace with HiltWorkerFactory when custom workers are implemented
        // For now, return default factory
        return object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: androidx.work.WorkerParameters
            ): androidx.work.ListenableWorker? {
                return null // Let WorkManager use default factory
            }
        }
    }

    // TODO: Uncomment and implement when Room entities and database are created
    /*
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "steadymate_database"
        )
            .fallbackToDestructiveMigration() // Remove in production
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideHabitDao(database: AppDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideHabitEntryDao(database: AppDatabase): HabitEntryDao {
        return database.habitEntryDao()
    }
    */
}

/**
 * Qualifier annotation for Main CoroutineDispatcher.
 * Use for UI operations and main thread work.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherMain

/**
 * Qualifier annotation for IO CoroutineDispatcher.
 * Use for network requests, file I/O, and database operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherIO

/**
 * Qualifier annotation for Default CoroutineDispatcher.
 * Use for CPU-intensive tasks like JSON parsing, sorting, filtering.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherDefault

/**
 * Qualifier annotation for Unconfined CoroutineDispatcher.
 * Use sparingly for testing or when dispatcher doesn't matter.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherUnconfined
