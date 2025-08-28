package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.steadymate.app.domain.model.HabitTick

/**
 * Room entity representing a habit completion in the database.
 * Simplified to match requirements - binary completion tracking.
 */
@Entity(
    tableName = "habit_ticks",
    primaryKeys = ["habitId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["date"])
    ]
)
data class HabitTickEntity(
    val habitId: String,
    val date: String, // yyyy-MM-dd
    val done: Boolean
)

/**
 * Extension function to convert HabitTickEntity to domain HabitTick model
 */
fun HabitTickEntity.toDomainModel(): HabitTick {
    return HabitTick(
        habitId = habitId,
        date = date,
        done = done
    )
}

/**
 * Extension function to convert domain HabitTick model to HabitTickEntity
 */
fun HabitTick.toEntity(): HabitTickEntity {
    return HabitTickEntity(
        habitId = habitId,
        date = date,
        done = done
    )
}
