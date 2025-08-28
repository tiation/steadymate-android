package com.steadymate.app.data.repository

import androidx.datastore.core.DataStore
import com.steadymate.app.data.proto.ConsentSettings
import com.steadymate.app.data.proto.NotificationSettings
import com.steadymate.app.data.proto.OnboardingPrefs
import com.steadymate.app.data.proto.OnboardingStatus
import com.steadymate.app.data.proto.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of OnboardingPreferencesRepository using Proto DataStore
 */
@Singleton
class OnboardingPreferencesRepositoryImpl @Inject constructor(
    private val onboardingPrefsDataStore: DataStore<OnboardingPrefs>
) : OnboardingPreferencesRepository {
    
    override fun getOnboardingPrefs(): Flow<OnboardingPrefs> = onboardingPrefsDataStore.data
    
    override suspend fun updateTheme(theme: Theme) {
        onboardingPrefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setTheme(theme)
                .build()
        }
    }
    
    override suspend fun updateConsentSettings(consentSettings: ConsentSettings) {
        onboardingPrefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setConsentSettings(consentSettings)
                .build()
        }
    }
    
    override suspend fun updateNotificationSettings(notificationSettings: NotificationSettings) {
        onboardingPrefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setNotificationSettings(notificationSettings)
                .build()
        }
    }
    
    override suspend fun updateOnboardingStatus(onboardingStatus: OnboardingStatus) {
        onboardingPrefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setOnboardingStatus(onboardingStatus)
                .build()
        }
    }
    
    override suspend fun markScreenCompleted(screenIndex: Int) {
        onboardingPrefsDataStore.updateData { currentPrefs ->
            val currentStatus = currentPrefs.onboardingStatus
            val updatedStatus = when (screenIndex) {
                0 -> currentStatus.toBuilder().setWelcomeCompleted(true)
                1 -> currentStatus.toBuilder().setPermissionsCompleted(true)
                2 -> currentStatus.toBuilder().setPreferencesCompleted(true)
                3 -> currentStatus.toBuilder().setProfileSetupCompleted(true)
                else -> currentStatus.toBuilder()
            }
                .setCurrentStep(screenIndex + 1)
                .build()
                
            currentPrefs.toBuilder()
                .setOnboardingStatus(updatedStatus)
                .build()
        }
    }
    
    override suspend fun completeOnboarding() {
        onboardingPrefsDataStore.updateData { currentPrefs ->
            val completedTimestamp = System.currentTimeMillis()
            val completedStatus = currentPrefs.onboardingStatus.toBuilder()
                .setIsCompleted(true)
                .setCompletedTimestamp(completedTimestamp)
                .setCurrentStep(4) // All 4 steps completed
                .setTotalSteps(4)
                .setWelcomeCompleted(true)
                .setPermissionsCompleted(true)
                .setPreferencesCompleted(true)
                .setProfileSetupCompleted(true)
                .build()
                
            currentPrefs.toBuilder()
                .setOnboardingStatus(completedStatus)
                .build()
        }
    }
    
    override suspend fun resetOnboarding() {
        onboardingPrefsDataStore.updateData { currentPrefs ->
            val resetStatus = OnboardingStatus.newBuilder()
                .setIsCompleted(false)
                .setCurrentStep(0)
                .setTotalSteps(4)
                .setStartedTimestamp(System.currentTimeMillis())
                .setCompletedTimestamp(0)
                .setWelcomeCompleted(false)
                .setPermissionsCompleted(false)
                .setPreferencesCompleted(false)
                .setProfileSetupCompleted(false)
                .build()
                
            currentPrefs.toBuilder()
                .setOnboardingStatus(resetStatus)
                .build()
        }
    }
    
    override fun isOnboardingCompleted(): Flow<Boolean> = 
        onboardingPrefsDataStore.data.map { prefs ->
            prefs.onboardingStatus.isCompleted
        }
    
    override fun getCurrentOnboardingStep(): Flow<Int> = 
        onboardingPrefsDataStore.data.map { prefs ->
            prefs.onboardingStatus.currentStep
        }
    
    override fun getThemeSettings(): Flow<Theme> = 
        onboardingPrefsDataStore.data.map { prefs ->
            prefs.theme
        }
    
    override fun getConsentSettings(): Flow<ConsentSettings> = 
        onboardingPrefsDataStore.data.map { prefs ->
            prefs.consentSettings
        }
    
    override fun getNotificationSettings(): Flow<NotificationSettings> = 
        onboardingPrefsDataStore.data.map { prefs ->
            prefs.notificationSettings
        }
}
