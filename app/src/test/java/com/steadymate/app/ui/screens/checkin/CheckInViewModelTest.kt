package com.steadymate.app.ui.screens.checkin

import app.cash.turbine.test
import com.steadymate.app.domain.model.MoodEntry
import com.steadymate.app.domain.model.User
import com.steadymate.app.domain.repository.MoodRepository
import com.steadymate.app.domain.usecase.GetCurrentUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.toLocalDate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CheckInViewModelTest {

    private lateinit var moodRepository: MoodRepository
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var viewModel: CheckInViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    @BeforeEach
    fun setUp() {
        moodRepository = mockk(relaxed = true)
        getCurrentUserUseCase = mockk(relaxed = true)

        // Default mock behaviors
        every { getCurrentUserUseCase() } returns flowOf(createTestUser())
        coEvery { moodRepository.getRecentMoodEntries(any(), any()) } returns emptyList()
        coEvery { moodRepository.insertMoodEntry(any()) } returns 1L
    }

    @Test
    fun `initial state should be correct`() = runTest(testDispatcher) {
        // When
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(0, uiState.moodLevel)
        assertTrue(uiState.selectedTags.isEmpty())
        assertEquals("", uiState.notes)
        assertFalse(uiState.isValidInput)
        assertFalse(uiState.isSubmitting)
        assertFalse(uiState.isSubmitted)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `updateMoodLevel should update state correctly`() = runTest(testDispatcher) {
        // Given
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        val moodLevel = 7

        // When
        viewModel.updateMoodLevel(moodLevel)

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(moodLevel, uiState.moodLevel)
        assertFalse(uiState.isValidInput) // Still false because no tags selected
    }

    @Test
    fun `toggleTag should add and remove tags correctly`() = runTest(testDispatcher) {
        // Given
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        val tag = "Happy"

        // When - Add tag
        viewModel.toggleTag(tag)

        // Then
        var uiState = viewModel.uiState.value
        assertTrue(uiState.selectedTags.contains(tag))

        // When - Remove tag
        viewModel.toggleTag(tag)

        // Then
        uiState = viewModel.uiState.value
        assertFalse(uiState.selectedTags.contains(tag))
    }

    @Test
    fun `isValidInput should be true when mood level and tags are set`() = runTest(testDispatcher) {
        // Given
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)

        // When
        viewModel.updateMoodLevel(7)
        viewModel.toggleTag("Happy")

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isValidInput)
    }

    @Test
    fun `updateNotes should update notes in state`() = runTest(testDispatcher) {
        // Given
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        val notes = "Feeling great today!"

        // When
        viewModel.updateNotes(notes)

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(notes, uiState.notes)
    }

    @Test
    fun `submitCheckIn should save mood entry when valid`() = runTest(testDispatcher) {
        // Given
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        viewModel.updateMoodLevel(8)
        viewModel.toggleTag("Happy")
        viewModel.updateNotes("Great day!")

        // When
        viewModel.submitCheckIn()
        advanceUntilIdle()

        // Then
        coVerify {
            moodRepository.insertMoodEntry(
                match { entry ->
                    entry.moodLevel == 8 &&
                    entry.emotionTags == listOf("Happy") &&
                    entry.notes == "Great day!" &&
                    entry.userId == "user_1" // TODO: Update when real user ID is implemented
                }
            )
        }

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isSubmitting)
        assertTrue(uiState.isSubmitted)
        assertTrue(uiState.hasCheckedInToday)
    }

    @Test
    fun `submitCheckIn should not save when invalid input`() = runTest(testDispatcher) {
        // Given
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        // Don't set valid input (missing mood level and tags)

        // When
        viewModel.submitCheckIn()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { moodRepository.insertMoodEntry(any()) }

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isSubmitting)
        assertFalse(uiState.isSubmitted)
    }

    @Test
    fun `submitCheckIn should handle repository error`() = runTest(testDispatcher) {
        // Given
        coEvery { moodRepository.insertMoodEntry(any()) } throws RuntimeException("Database error")
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        viewModel.updateMoodLevel(7)
        viewModel.toggleTag("Happy")

        // When
        viewModel.submitCheckIn()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isSubmitting)
        assertFalse(uiState.isSubmitted)
        assertNotNull(uiState.errorMessage)
        assertTrue(uiState.errorMessage!!.contains("Database error"))
    }

    @Test
    fun `clearError should remove error message`() = runTest(testDispatcher) {
        // Given
        coEvery { moodRepository.insertMoodEntry(any()) } throws RuntimeException("Test error")
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        viewModel.updateMoodLevel(7)
        viewModel.toggleTag("Happy")
        viewModel.submitCheckIn()
        advanceUntilIdle()

        // Verify error is set
        assertNotNull(viewModel.uiState.value.errorMessage)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `hasCheckedInToday should return true when entry exists for today`() = runTest(testDispatcher) {
        // Given
        val todayEntry = createTestMoodEntry(timestamp = now)
        coEvery { moodRepository.getRecentMoodEntries(any(), any()) } returns listOf(todayEntry)

        // When
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.hasCheckedInToday)
    }

    @Test
    fun `calculateStreak should return correct consecutive days`() = runTest(testDispatcher) {
        // Given - 3 consecutive days including today
        val entries = listOf(
            createTestMoodEntry(timestamp = now), // Today
            createTestMoodEntry(timestamp = now.toLocalDate().minus(DatePeriod(days = 1)).atTime(12, 0)), // Yesterday
            createTestMoodEntry(timestamp = now.toLocalDate().minus(DatePeriod(days = 2)).atTime(12, 0)), // Day before
            createTestMoodEntry(timestamp = now.toLocalDate().minus(DatePeriod(days = 4)).atTime(12, 0)) // Gap - shouldn't count
        )
        coEvery { moodRepository.getRecentMoodEntries(any(), any()) } returns entries

        // When
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(3, uiState.currentStreak)
    }

    @Test
    fun `updateChartPeriod should refresh chart data`() = runTest(testDispatcher) {
        // Given
        val weekEntries = listOf(createTestMoodEntry())
        val monthEntries = listOf(createTestMoodEntry(), createTestMoodEntry())
        
        coEvery { moodRepository.getRecentMoodEntries(any(), 7) } returns weekEntries
        coEvery { moodRepository.getRecentMoodEntries(any(), 30) } returns monthEntries

        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // When
        viewModel.updateChartPeriod(ChartPeriod.MONTH)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(ChartPeriod.MONTH, uiState.selectedChartPeriod)
        
        // Verify chart data was updated
        viewModel.chartData.test {
            val chartData = awaitItem()
            assertEquals(2, chartData.size) // Month entries
        }

        coVerify { moodRepository.getRecentMoodEntries(any(), 30) }
    }

    @Test
    fun `chartData should emit updates when entries change`() = runTest(testDispatcher) {
        // Given
        val entries = listOf(
            createTestMoodEntry(moodLevel = 7),
            createTestMoodEntry(moodLevel = 5)
        )
        coEvery { moodRepository.getRecentMoodEntries(any(), any()) } returns entries

        // When
        viewModel = CheckInViewModel(moodRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Then
        viewModel.chartData.test {
            val chartData = awaitItem()
            assertEquals(2, chartData.size)
            assertEquals(7, chartData[0].moodLevel)
            assertEquals(5, chartData[1].moodLevel)
        }
    }

    private fun createTestUser(): User {
        return User(
            id = "user_1",
            name = "Test User",
            email = "test@example.com",
            joinDate = now.toLocalDate()
        )
    }

    private fun createTestMoodEntry(
        userId: String = "user_1",
        moodLevel: Int = 7,
        timestamp: LocalDateTime = now
    ): MoodEntry {
        return MoodEntry(
            userId = userId,
            moodLevel = moodLevel,
            emotionTags = listOf("Happy"),
            notes = "Test entry",
            timestamp = timestamp
        )
    }
}
