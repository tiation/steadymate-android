package com.steadymate.app.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.steadymate.app.domain.model.SupportContact
import kotlinx.datetime.LocalDateTime

/**
 * Room entity representing a support contact in the database.
 * These are trusted contacts that can be reached during crisis situations.
 */
@Entity(
    tableName = "support_contacts",
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
        Index(value = ["isPrimary"]),
        Index(value = ["contactType"])
    ]
)
data class SupportContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val relationship: String, // e.g., "friend", "family", "therapist", "helpline"
    val contactType: ContactType = ContactType.PERSONAL,
    val isPrimary: Boolean = false,
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: LocalDateTime,
    val lastContacted: LocalDateTime? = null
)

enum class ContactType {
    PERSONAL, // Friends, family
    PROFESSIONAL, // Therapist, counselor
    EMERGENCY, // Crisis helplines
    MEDICAL // Doctor, psychiatrist
}

/**
 * Extension function to convert SupportContactEntity to domain SupportContact model
 */
fun SupportContactEntity.toDomainModel(): SupportContact {
    return SupportContact(
        id = id,
        userId = userId,
        name = name,
        phone = phone,
        email = email,
        relationship = relationship,
        contactType = contactType,
        isPrimary = isPrimary,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        lastContacted = lastContacted
    )
}

/**
 * Extension function to convert domain SupportContact model to SupportContactEntity
 */
fun SupportContact.toEntity(): SupportContactEntity {
    return SupportContactEntity(
        id = id,
        userId = userId,
        name = name,
        phone = phone,
        email = email,
        relationship = relationship,
        contactType = contactType,
        isPrimary = isPrimary,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        lastContacted = lastContacted
    )
}
