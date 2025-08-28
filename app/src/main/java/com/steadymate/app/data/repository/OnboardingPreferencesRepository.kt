package com.steadymate.app.data.repository

import com.steadymate.app.data.proto.ConsentSettings
import com.steadymate.app.data.proto.NotificationSettings
import com.steadymate.app.data.proto.OnboardingPrefs
import com.steadymate.app.data.proto.OnboardingStatus
import com.steadymate.app.data.proto.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing onboarding preferences using Proto DataStore
 */
interface OnboardingPreferencesRepository {
    
    /**
     * Get the complete onboarding preferences as a Flow
     */
    fun getOnboardingPrefs(): Flow<OnboardingPrefs>
    
    /**
     * Update theme settings
     */
    suspend fun updateTheme(theme: Theme)
    
    /**
     * Update consent settings
     */
    suspend fun updateConsentSettings(consentSettings: ConsentSettings)
    
    /**
     * Update notification settings
     */
    suspend fun updateNotificationSettings(notificationSettings: NotificationSettings)
    
    /**
     * Update onboarding status
     */
    suspend fun updateOnboardingStatus(onboardingStatus: OnboardingStatus)
    
    /**
     * Mark a specific onboarding screen as completed
     */
    suspend fun markScreenCompleted(screenIndex: Int)
    
    /**
     * Complete the entire onboarding flow
     */
    suspend fun completeOnboarding()
    
    /**
     * Reset onboarding status (useful for testing or re-onboarding)
     */
    suspend fun resetOnboarding()
    
    /**
     * Check if onboarding is completed
     */
    fun isOnboardingCompleted(): Flow<Boolean>
    
    /**
     * Get current onboarding step
     */
    fun getCurrentOnboardingStep(): Flow<Int>
    
    /**
     * Get theme settings
     */
    fun getThemeSettings(): Flow<Theme>
    
    /**
     * Get consent settings
     */
    fun getConsentSettings(): Flow<ConsentSettings>
    
    /**
     * Get notification settings
     */
    fun getNotificationSettings(): Flow<NotificationSettings>
}
