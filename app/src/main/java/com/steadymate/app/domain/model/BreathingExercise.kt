package com.steadymate.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a breathing exercise with timing patterns
 */
@Serializable
data class BreathingExercise(
    val id: String,
    val name: String,
    val description: String,
    val inhaleSeconds: Int,
    val holdInhaleSeconds: Int,
    val exhaleSeconds: Int,
    val holdExhaleSeconds: Int,
    val totalCycles: Int,
    val category: BreathingCategory,
    val difficulty: BreathingDifficulty,
    val benefits: List<String>
) {
    /**
     * Calculate total duration in seconds for the entire exercise
     */
    val totalDurationSeconds: Int
        get() = totalCycles * cycleDurationSeconds
    
    /**
     * Calculate duration in seconds for one complete breathing cycle
     */
    val cycleDurationSeconds: Int
        get() = inhaleSeconds + holdInhaleSeconds + exhaleSeconds + holdExhaleSeconds
    
    /**
     * Get formatted duration string for display
     */
    val formattedDuration: String
        get() {
            val minutes = totalDurationSeconds / 60
            val seconds = totalDurationSeconds % 60
            return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
        }

    companion object {
        /**
         * Predefined breathing exercise presets for men's mental health
         */
        val PRESETS = listOf(
            // Beginner-friendly exercises
            BreathingExercise(
                id = "box_breathing",
                name = "Box Breathing",
                description = "Equal timing for all phases - perfect for stress relief and focus",
                inhaleSeconds = 4,
                holdInhaleSeconds = 4,
                exhaleSeconds = 4,
                holdExhaleSeconds = 4,
                totalCycles = 6,
                category = BreathingCategory.STRESS_RELIEF,
                difficulty = BreathingDifficulty.BEGINNER,
                benefits = listOf("Reduces stress", "Improves focus", "Calms nervous system")
            ),
            
            BreathingExercise(
                id = "four_seven_eight",
                name = "4-7-8 Breathing",
                description = "Inhale for 4, hold for 7, exhale for 8 - excellent for anxiety and sleep",
                inhaleSeconds = 4,
                holdInhaleSeconds = 7,
                exhaleSeconds = 8,
                holdExhaleSeconds = 0,
                totalCycles = 4,
                category = BreathingCategory.ANXIETY_RELIEF,
                difficulty = BreathingDifficulty.INTERMEDIATE,
                benefits = listOf("Reduces anxiety", "Promotes sleep", "Activates relaxation response")
            ),
            
            BreathingExercise(
                id = "tactical_breathing",
                name = "Tactical Breathing",
                description = "Military-style breathing for high-pressure situations",
                inhaleSeconds = 4,
                holdInhaleSeconds = 4,
                exhaleSeconds = 4,
                holdExhaleSeconds = 4,
                totalCycles = 8,
                category = BreathingCategory.PERFORMANCE,
                difficulty = BreathingDifficulty.INTERMEDIATE,
                benefits = listOf("Maintains composure", "Improves decision-making", "Controls adrenaline")
            ),
            
            BreathingExercise(
                id = "coherent_breathing",
                name = "Coherent Breathing",
                description = "5-second inhale and exhale for heart rate variability",
                inhaleSeconds = 5,
                holdInhaleSeconds = 0,
                exhaleSeconds = 5,
                holdExhaleSeconds = 0,
                totalCycles = 12,
                category = BreathingCategory.HEART_HEALTH,
                difficulty = BreathingDifficulty.BEGINNER,
                benefits = listOf("Improves HRV", "Balances autonomic system", "Increases coherence")
            ),
            
            // More advanced exercises
            BreathingExercise(
                id = "extended_exhale",
                name = "Extended Exhale",
                description = "Longer exhale activates the parasympathetic nervous system",
                inhaleSeconds = 4,
                holdInhaleSeconds = 2,
                exhaleSeconds = 8,
                holdExhaleSeconds = 0,
                totalCycles = 6,
                category = BreathingCategory.RELAXATION,
                difficulty = BreathingDifficulty.INTERMEDIATE,
                benefits = listOf("Deep relaxation", "Reduces blood pressure", "Calms mind")
            ),
            
            BreathingExercise(
                id = "energizing_breath",
                name = "Energizing Breath",
                description = "Quick, energizing breathing to boost alertness",
                inhaleSeconds = 2,
                holdInhaleSeconds = 1,
                exhaleSeconds = 3,
                holdExhaleSeconds = 0,
                totalCycles = 10,
                category = BreathingCategory.ENERGY,
                difficulty = BreathingDifficulty.BEGINNER,
                benefits = listOf("Increases alertness", "Boosts energy", "Improves concentration")
            ),
            
            BreathingExercise(
                id = "resonant_breathing",
                name = "Resonant Breathing",
                description = "6 breaths per minute for optimal physiological balance",
                inhaleSeconds = 5,
                holdInhaleSeconds = 0,
                exhaleSeconds = 5,
                holdExhaleSeconds = 0,
                totalCycles = 15,
                category = BreathingCategory.BALANCE,
                difficulty = BreathingDifficulty.ADVANCED,
                benefits = listOf("Optimal HRV", "Stress resilience", "Emotional balance")
            )
        )
        
        /**
         * Get exercise by ID
         */
        fun getById(id: String): BreathingExercise? = PRESETS.find { it.id == id }
        
        /**
         * Get exercises by category
         */
        fun getByCategory(category: BreathingCategory): List<BreathingExercise> = 
            PRESETS.filter { it.category == category }
            
        /**
         * Get exercises by difficulty
         */
        fun getByDifficulty(difficulty: BreathingDifficulty): List<BreathingExercise> = 
            PRESETS.filter { it.difficulty == difficulty }
    }
}

enum class BreathingCategory(val displayName: String, val description: String) {
    STRESS_RELIEF("Stress Relief", "Techniques to reduce stress and tension"),
    ANXIETY_RELIEF("Anxiety Relief", "Exercises to calm anxiety and worry"),
    RELAXATION("Relaxation", "Deep relaxation and nervous system reset"),
    PERFORMANCE("Performance", "Maintain composure under pressure"),
    ENERGY("Energy", "Boost alertness and vitality"),
    HEART_HEALTH("Heart Health", "Improve cardiovascular coherence"),
    BALANCE("Balance", "Restore physiological and emotional balance")
}

enum class BreathingDifficulty(val displayName: String, val description: String) {
    BEGINNER("Beginner", "Easy to follow, suitable for newcomers"),
    INTERMEDIATE("Intermediate", "Moderate complexity, some experience helpful"),
    ADVANCED("Advanced", "Complex patterns, requires practice")
}

/**
 * Represents the current phase of a breathing cycle
 */
enum class BreathingPhase(val displayName: String, val instruction: String) {
    INHALE("Inhale", "Breathe in slowly and deeply"),
    HOLD_INHALE("Hold", "Hold your breath"),
    EXHALE("Exhale", "Breathe out completely"),
    HOLD_EXHALE("Hold", "Keep lungs empty"),
    COMPLETE("Complete", "Exercise finished")
}

/**
 * State for a breathing session in progress
 */
data class BreathingSessionState(
    val exercise: BreathingExercise,
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val currentCycle: Int = 0,
    val currentPhase: BreathingPhase = BreathingPhase.INHALE,
    val secondsInCurrentPhase: Int = 0,
    val totalElapsedSeconds: Int = 0,
    val animationScale: Float = 0.5f
) {
    val isComplete: Boolean
        get() = currentCycle >= exercise.totalCycles
        
    val progressPercentage: Float
        get() = if (exercise.totalCycles > 0) {
            (currentCycle.toFloat() / exercise.totalCycles.toFloat()) * 100f
        } else 0f
        
    val remainingSeconds: Int
        get() = exercise.totalDurationSeconds - totalElapsedSeconds
        
    val formattedRemainingTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}
