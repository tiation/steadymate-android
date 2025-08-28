package com.steadymate.app

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import androidx.hilt.work.HiltWorkerFactory
import javax.inject.Inject

/**
 * Application class for SteadyMate app.
 * 
 * This class is annotated with @HiltAndroidApp to trigger Hilt's code generation
 * including a base class for the application that serves as the application-level
 * dependency container.
 * 
 * Also implements Configuration.Provider to customize WorkManager configuration.
 */
@HiltAndroidApp
class SteadyMateApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}
