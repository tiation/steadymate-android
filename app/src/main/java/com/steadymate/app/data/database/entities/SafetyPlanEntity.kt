package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.steadymate.app.domain.model.SafetyPlan
import kotlinx.datetime.LocalDateTime

/**
 * Room entity representing a safety plan in the database.
 * This stores crisis management information including warning signs, coping strategies, and support contacts.
 */
@Entity(
    tableName = "safety_plans",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lastUpdated"])
    ]
)
data class SafetyPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val warningSigns: List<String>,
    val copingStrategies: List<String>,
    val reasonsToLive: List<String>,
    val distractionActivities: List<String>,
    val lastUpdated: LocalDateTime,
    val isActive: Boolean = true
)

/**
 * Extension function to convert SafetyPlanEntity to domain SafetyPlan model
 */
fun SafetyPlanEntity.toDomainModel(): SafetyPlan {
    return SafetyPlan(
        id = id,
        userId = userId,
        warningSigns = warningSigns,
        copingStrategies = copingStrategies,
        reasonsToLive = reasonsToLive,
        distractionActivities = distractionActivities,
        lastUpdated = lastUpdated,
        isActive = isActive
    )
}

/**
 * Extension function to convert domain SafetyPlan model to SafetyPlanEntity
 */
fun SafetyPlan.toEntity(): SafetyPlanEntity {
    return SafetyPlanEntity(
        id = id,
        userId = userId,
        warningSigns = warningSigns,
        copingStrategies = copingStrategies,
        reasonsToLive = reasonsToLive,
        distractionActivities = distractionActivities,
        lastUpdated = lastUpdated,
        isActive = isActive
    )
}
