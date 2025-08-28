package com.steadymate.app.domain.model

import com.steadymate.app.data.database.entities.ContactType
import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing a support contact.
 * These are trusted contacts that can be reached during crisis situations or for general support.
 */
data class SupportContact(
    val id: Long = 0,
    val userId: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val relationship: String, // e.g., "friend", "family", "therapist", "helpline"
    val contactType: ContactType = ContactType.PERSONAL,
    val isPrimary: Boolean = false, // Primary emergency contact
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: LocalDateTime,
    val lastContacted: LocalDateTime? = null
)
