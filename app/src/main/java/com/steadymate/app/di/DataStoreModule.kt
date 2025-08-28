package com.steadymate.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.steadymate.app.data.datastore.OnboardingPrefsSerializer
import com.steadymate.app.data.proto.OnboardingPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Module for providing Proto DataStore instances
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private const val ONBOARDING_PREFERENCES_FILE_NAME = "onboarding_preferences.pb"
    
    /**
     * Provides OnboardingPrefs Proto DataStore
     */
    @Provides
    @Singleton
    fun provideOnboardingPrefsDataStore(
        @ApplicationContext context: Context,
        onboardingPrefsSerializer: OnboardingPrefsSerializer
    ): DataStore<OnboardingPrefs> {
        return DataStoreFactory.create(
            serializer = onboardingPrefsSerializer,
            produceFile = { context.dataStoreFile(ONBOARDING_PREFERENCES_FILE_NAME) },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}
