package com.steadymate.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.steadymate.app.data.database.SteadyDb
import com.steadymate.app.data.database.dao.MoodEntryDao
import com.steadymate.app.domain.model.MoodEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.toLocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoodRepositoryImplTest {

    private lateinit var database: SteadyDb
    private lateinit var moodEntryDao: MoodEntryDao
    private lateinit var repository: MoodRepositoryImpl
    
    private val testDispatcher = StandardTestDispatcher()
    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    @BeforeEach
    fun setUp() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SteadyDb::class.java
        ).allowMainThreadQueries()
            .build()

        moodEntryDao = database.moodEntryDao()
        repository = MoodRepositoryImpl(moodEntryDao, testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertMoodEntry should save mood entry to database`() = runTest(testDispatcher) {
        // Given
        val moodEntry = createTestMoodEntry()

        // When
        val entryId = repository.insertMoodEntry(moodEntry)

        // Then
        assertTrue(entryId > 0)
        
        val retrievedEntry = repository.getMoodEntryById(entryId)
        assertNotNull(retrievedEntry)
        assertEquals(moodEntry.moodLevel, retrievedEntry!!.moodLevel)
        assertEquals(moodEntry.emotionTags, retrievedEntry.emotionTags)
        assertEquals(moodEntry.notes, retrievedEntry.notes)
    }

    @Test
    fun `getMoodEntriesByUser should return entries for specific user`() = runTest(testDispatcher) {
        // Given
        val userId = "test_user_1"
        val otherUserId = "test_user_2"
        
        val entry1 = createTestMoodEntry(userId = userId, moodLevel = 7)
        val entry2 = createTestMoodEntry(userId = userId, moodLevel = 5)
        val entryForOtherUser = createTestMoodEntry(userId = otherUserId, moodLevel = 8)

        // When
        repository.insertMoodEntry(entry1)
        repository.insertMoodEntry(entry2)
        repository.insertMoodEntry(entryForOtherUser)

        val userEntries = repository.getMoodEntriesByUser(userId)

        // Then
        assertEquals(2, userEntries.size)
        assertTrue(userEntries.all { it.userId == userId })
    }

    @Test
    fun `getMoodEntriesInDateRange should filter by date correctly`() = runTest(testDispatcher) {
        // Given
        val userId = "test_user"
        val startDate = now.toLocalDate().minus(DatePeriod(days = 5)).atTime(0, 0)
        val endDate = now.toLocalDate().minus(DatePeriod(days = 1)).atTime(23, 59)
        
        val entryInRange = createTestMoodEntry(
            userId = userId,
            timestamp = now.toLocalDate().minus(DatePeriod(days = 3)).atTime(12, 0)
        )
        val entryBeforeRange = createTestMoodEntry(
            userId = userId,
            timestamp = now.toLocalDate().minus(DatePeriod(days = 10)).atTime(12, 0)
        )
        val entryAfterRange = createTestMoodEntry(
            userId = userId,
            timestamp = now
        )

        // When
        repository.insertMoodEntry(entryInRange)
        repository.insertMoodEntry(entryBeforeRange)
        repository.insertMoodEntry(entryAfterRange)

        val entriesInRange = repository.getMoodEntriesInDateRange(userId, startDate, endDate)

        // Then
        assertEquals(1, entriesInRange.size)
        assertEquals(entryInRange.moodLevel, entriesInRange.first().moodLevel)
    }

    @Test
    fun `getRecentMoodEntries should limit results correctly`() = runTest(testDispatcher) {
        // Given
        val userId = "test_user"
        val limit = 3
        
        // Insert 5 entries
        repeat(5) { i ->
            repository.insertMoodEntry(
                createTestMoodEntry(
                    userId = userId,
                    moodLevel = i + 1,
                    timestamp = now.toLocalDate().minus(DatePeriod(days = i)).atTime(12, 0)
                )
            )
        }

        // When
        val recentEntries = repository.getRecentMoodEntries(userId, limit)

        // Then
        assertEquals(limit, recentEntries.size)
        // Should be ordered by timestamp DESC (most recent first)
        assertTrue(recentEntries[0].timestamp > recentEntries[1].timestamp)
    }

    @Test
    fun `getAverageMoodSince should calculate average correctly`() = runTest(testDispatcher) {
        // Given
        val userId = "test_user"
        val sinceDate = now.toLocalDate().minus(DatePeriod(days = 5)).atTime(0, 0)
        
        // Insert entries with mood levels: 2, 4, 6, 8 (average = 5.0)
        val moodLevels = listOf(2, 4, 6, 8)
        moodLevels.forEachIndexed { index, level ->
            repository.insertMoodEntry(
                createTestMoodEntry(
                    userId = userId,
                    moodLevel = level,
                    timestamp = now.toLocalDate().minus(DatePeriod(days = index)).atTime(12, 0)
                )
            )
        }

        // When
        val average = repository.getAverageMoodSince(userId, sinceDate)

        // Then
        assertNotNull(average)
        assertEquals(5.0, average!!, 0.01)
    }

    @Test
    fun `getMoodEntryCount should return correct count`() = runTest(testDispatcher) {
        // Given
        val userId = "test_user"
        val expectedCount = 3
        
        repeat(expectedCount) {
            repository.insertMoodEntry(createTestMoodEntry(userId = userId))
        }

        // When
        val count = repository.getMoodEntryCount(userId)

        // Then
        assertEquals(expectedCount, count)
    }

    @Test
    fun `updateMoodEntry should modify existing entry`() = runTest(testDispatcher) {
        // Given
        val originalEntry = createTestMoodEntry(moodLevel = 5, notes = "Original")
        val entryId = repository.insertMoodEntry(originalEntry)
        
        val updatedEntry = originalEntry.copy(
            id = entryId,
            moodLevel = 8,
            notes = "Updated"
        )

        // When
        repository.updateMoodEntry(updatedEntry)

        // Then
        val retrievedEntry = repository.getMoodEntryById(entryId)
        assertNotNull(retrievedEntry)
        assertEquals(8, retrievedEntry!!.moodLevel)
        assertEquals("Updated", retrievedEntry.notes)
    }

    @Test
    fun `deleteMoodEntryById should remove entry from database`() = runTest(testDispatcher) {
        // Given
        val moodEntry = createTestMoodEntry()
        val entryId = repository.insertMoodEntry(moodEntry)

        // When
        repository.deleteMoodEntryById(entryId)

        // Then
        val retrievedEntry = repository.getMoodEntryById(entryId)
        assertNull(retrievedEntry)
    }

    @Test
    fun `getMoodEntriesByUserFlow should emit updates`() = runTest(testDispatcher) {
        // Given
        val userId = "test_user"

        // When - Initial state (empty)
        val flow = repository.getMoodEntriesByUserFlow(userId)
        val initialEntries = flow.first()

        // Then
        assertTrue(initialEntries.isEmpty())

        // When - Add entry
        repository.insertMoodEntry(createTestMoodEntry(userId = userId))
        val updatedEntries = flow.first()

        // Then
        assertEquals(1, updatedEntries.size)
    }

    private fun createTestMoodEntry(
        userId: String = "test_user",
        moodLevel: Int = 7,
        emotionTags: List<String> = listOf("Happy", "Relaxed"),
        notes: String = "Test mood entry",
        timestamp: LocalDateTime = now
    ): MoodEntry {
        return MoodEntry(
            userId = userId,
            moodLevel = moodLevel,
            emotionTags = emotionTags,
            notes = notes,
            timestamp = timestamp
        )
    }
}
