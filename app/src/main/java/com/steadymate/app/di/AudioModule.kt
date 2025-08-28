package com.steadymate.app.di

import android.content.Context
import com.steadymate.app.ui.utils.SoundManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module providing audio-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    
    /**
     * Provides SoundManager singleton instance
     */
    @Provides
    @Singleton
    fun provideSoundManager(
        @ApplicationContext context: Context
    ): SoundManager {
        return SoundManager(context)
    }
}
