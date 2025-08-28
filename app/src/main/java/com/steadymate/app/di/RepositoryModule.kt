package com.steadymate.app.di

import com.steadymate.app.data.repository.*
import com.steadymate.app.domain.repository.*
// BuildConfig import removed - using mock repository for now
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Repository module for binding repository interfaces to their implementations.
 * This module uses @Binds instead of @Provides for interface bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMoodRepository(
        moodRepositoryImpl: MoodRepositoryImpl
    ): MoodRepository
    
    @Binds
    @Singleton
    abstract fun bindCBTRepository(
        cbtRepositoryImpl: CBTRepositoryImpl
    ): CBTRepository
    
    @Binds
    @Singleton
    abstract fun bindBreathingSessionRepository(
        mockBreathingSessionRepository: MockBreathingSessionRepository
    ): BreathingSessionRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        habitRepositoryImpl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindHabitTickRepository(
        habitTickRepositoryImpl: HabitTickRepositoryImpl
    ): HabitTickRepository

    companion object {
        @Provides
        @Singleton
        fun provideInsightsRepository(
            insightsRepositoryImpl: InsightsRepositoryImpl
        ): InsightsRepository {
            // Use mock data for now to avoid compilation issues
            return insightsRepositoryImpl
        }
        
        @Provides
        @Singleton
        @Named("mock")
        fun provideMockInsightsRepository(
            insightsRepositoryImpl: InsightsRepositoryImpl
        ): InsightsRepository = insightsRepositoryImpl
    }

    @Binds
    @Singleton
    abstract fun bindOnboardingPreferencesRepository(
        onboardingPreferencesRepositoryImpl: OnboardingPreferencesRepositoryImpl
    ): OnboardingPreferencesRepository
}
