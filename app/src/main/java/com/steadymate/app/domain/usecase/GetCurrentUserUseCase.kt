package com.steadymate.app.domain.usecase

import com.steadymate.app.domain.model.User
import com.steadymate.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the current user.
 * This encapsulates the business logic for retrieving the current user,
 * following clean architecture principles.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    
    /**
     * Execute the use case to get the current user.
     * @return Flow emitting the current user or null if not found
     */
    operator fun invoke(): Flow<User?> {
        return userRepository.getCurrentUser()
    }
}
