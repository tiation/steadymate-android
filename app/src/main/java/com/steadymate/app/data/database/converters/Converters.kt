package com.steadymate.app.data.database.converters

import androidx.room.TypeConverter
import com.steadymate.app.data.database.entities.ContactType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room type converters for complex data types used in SteadyMate app.
 * These converters allow Room to persist non-primitive types to the SQLite database.
 */
class Converters {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    // List<String> converters
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            json.decodeFromString<List<String>>(value)
        }
    }
    
    // LocalDateTime converters
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String {
        return value.toString()
    }
    
    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }
    
    // LocalDate converters
    @TypeConverter
    fun fromLocalDate(value: LocalDate): String {
        return value.toString()
    }
    
    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }
    
    // LocalTime converters
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.toString()
    }
    
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }
    
    // ContactType converters
    @TypeConverter
    fun fromContactType(value: ContactType): String {
        return value.name
    }
    
    @TypeConverter
    fun toContactType(value: String): ContactType {
        return ContactType.valueOf(value)
    }
}
