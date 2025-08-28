package com.steadymate.app.domain.repository

import com.steadymate.app.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related data operations.
 * This interface defines the contract for data access without exposing implementation details.
 * It belongs in the domain layer to maintain clean architecture principles.
 */
interface UserRepository {
    
    /**
     * Get a flow of the current user.
     * @return Flow emitting the current user or null if not found
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * Get user by ID.
     * @param userId The unique identifier of the user
     * @return Flow emitting the user or null if not found
     */
    fun getUserById(userId: String): Flow<User?>
    
    /**
     * Save or update user information.
     * @param user The user to save or update
     */
    suspend fun saveUser(user: User)
    
    /**
     * Delete user by ID.
     * @param userId The unique identifier of the user to delete
     */
    suspend fun deleteUser(userId: String)
    
    /**
     * Update user streak count.
     * @param userId The unique identifier of the user
     * @param newStreak The new streak count
     */
    suspend fun updateUserStreak(userId: String, newStreak: Int)
}
