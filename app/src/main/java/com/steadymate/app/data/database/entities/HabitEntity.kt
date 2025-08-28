package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.steadymate.app.domain.model.Habit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Room entity representing a habit in the database.
 * Simplified to match requirements - small, binary habits with optional reminders.
 */
@Entity(
    tableName = "habits",
    indices = [
        Index(value = ["enabled"]),
        Index(value = ["reminderTime"])
    ]
)
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val schedule: String, // e.g., "MTWTF--" for weekdays
    val reminderTime: String? = null, // "07:30" format
    val enabled: Boolean = true
)

/**
 * Extension function to convert HabitEntity to domain Habit model
 */
fun HabitEntity.toDomainModel(): Habit {
    return Habit(
        id = id,
        title = title,
        schedule = schedule,
        reminderTime = reminderTime,
        enabled = enabled
    )
}

/**
 * Extension function to convert domain Habit model to HabitEntity
 */
fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        title = title,
        schedule = schedule,
        reminderTime = reminderTime,
        enabled = enabled
    )
}
