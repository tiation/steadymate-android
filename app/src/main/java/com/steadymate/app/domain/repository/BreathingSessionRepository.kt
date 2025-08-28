package com.steadymate.app.domain.repository

import com.steadymate.app.domain.model.BreathingSession

/**
 * Simple mock repository for breathing sessions during MVP phase
 */
interface BreathingSessionRepository {
    suspend fun insertBreathingSession(session: BreathingSession)
    suspend fun getAllBreathingSessions(): List<BreathingSession>
    suspend fun getBreathingSessionsByUserId(userId: String): List<BreathingSession>
}

/**
 * Mock implementation that doesn't persist data - for MVP only
 */
class MockBreathingSessionRepository @javax.inject.Inject constructor() : BreathingSessionRepository {
    override suspend fun insertBreathingSession(session: BreathingSession) {
        // Mock implementation - no persistence for MVP
    }
    
    override suspend fun getAllBreathingSessions(): List<BreathingSession> {
        return emptyList()
    }
    
    override suspend fun getBreathingSessionsByUserId(userId: String): List<BreathingSession> {
        return emptyList()
    }
}
