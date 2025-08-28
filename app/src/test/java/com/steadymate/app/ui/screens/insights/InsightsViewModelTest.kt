package com.steadymate.app.ui.screens.insights

import app.cash.turbine.test
import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.InsightsRepository
import com.steadymate.app.domain.repository.TimeRange
import com.steadymate.app.domain.usecase.GetCurrentUserUseCase
import com.steadymate.app.domain.model.User
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith
class InsightsViewModelTest {

    private lateinit var insightsRepository: InsightsRepository
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var viewModel: InsightsViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    @BeforeEach
    fun setUp() {
        insightsRepository = mockk(relaxed = true)
        getCurrentUserUseCase = mockk(relaxed = true)

        // Default mock behaviors
        every { getCurrentUserUseCase() } returns flowOf(createTestUser())
        coEvery { insightsRepository.generateInsights(any(), any()) } returns emptyList()
        coEvery { insightsRepository.getMoodStatistics(any(), any()) } returns createTestMoodStatistics()
        coEvery { insightsRepository.analyzeEmotions(any(), any()) } returns emptyList()
        coEvery { insightsRepository.analyzeActivityCorrelations(any(), any()) } returns emptyList()
        coEvery { insightsRepository.getChartData(any(), any()) } returns emptyList()
        every { insightsRepository.getMoodTrendDataFlow(any(), any()) } returns flowOf(emptyList())
    }

    @Test
    fun `initial state should be correct`() = runTest(testDispatcher) {
        // When
        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals(TimeRange.MONTH, uiState.selectedTimeRange)
        assertNull(uiState.errorMessage)
        assertTrue(uiState.insights.isEmpty())
    }

    @Test
    fun `loadInsights should update state correctly`() = runTest(testDispatcher) {
        // Given
        val testInsights = listOf(createTestInsight())
        val testStatistics = createTestMoodStatistics()
        val testEmotions = listOf(createTestEmotionAnalysis())
        val testActivities = listOf(createTestActivityCorrelation())

        coEvery { insightsRepository.generateInsights(any(), any()) } returns testInsights
        coEvery { insightsRepository.getMoodStatistics(any(), any()) } returns testStatistics
        coEvery { insightsRepository.analyzeEmotions(any(), any()) } returns testEmotions
        coEvery { insightsRepository.analyzeActivityCorrelations(any(), any()) } returns testActivities

        // When
        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals(testInsights, uiState.insights)
        assertEquals(testStatistics, uiState.moodStatistics)
        assertEquals(testEmotions, uiState.emotionAnalysis)
        assertEquals(testActivities, uiState.activityCorrelations)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `selectTimeRange should update state and reload insights`() = runTest(testDispatcher) {
        // Given
        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()
        clearMocks(insightsRepository)

        // When
        viewModel.selectTimeRange(TimeRange.WEEK)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(TimeRange.WEEK, uiState.selectedTimeRange)
        
        // Verify insights were reloaded with new time range
        coVerify { insightsRepository.generateInsights(any(), TimeRange.WEEK) }
        coVerify { insightsRepository.getMoodStatistics(any(), TimeRange.WEEK) }
    }

    @Test
    fun `refreshInsights should reload all data`() = runTest(testDispatcher) {
        // Given
        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()
        clearMocks(insightsRepository)

        // When
        viewModel.refreshInsights()
        advanceUntilIdle()

        // Then
        coVerify { insightsRepository.generateInsights(any(), any()) }
        coVerify { insightsRepository.getMoodStatistics(any(), any()) }
        coVerify { insightsRepository.analyzeEmotions(any(), any()) }
        coVerify { insightsRepository.analyzeActivityCorrelations(any(), any()) }
    }

    @Test
    fun `chartData flow should emit updates when time range changes`() = runTest(testDispatcher) {
        // Given
        val testChartData = listOf(createTestChartDataPoint())
        coEvery { insightsRepository.getChartData(any(), any()) } returns testChartData

        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // When & Then
        viewModel.chartData.test {
            // Should emit chart data
            val chartData = awaitItem()
            assertEquals(testChartData, chartData)
        }
    }

    @Test
    fun `getInsightByType should return correct insight`() = runTest(testDispatcher) {
        // Given
        val trendInsight = createTestInsight(InsightType.MOOD_TREND)
        val emotionInsight = createTestInsight(InsightType.EMOTION_PATTERN)
        val testInsights = listOf(trendInsight, emotionInsight)

        coEvery { insightsRepository.generateInsights(any(), any()) } returns testInsights

        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // When & Then
        val foundInsight = viewModel.getInsightByType(InsightType.MOOD_TREND)
        assertEquals(trendInsight, foundInsight)

        val notFoundInsight = viewModel.getInsightByType(InsightType.WARNING_SIGN)
        assertNull(notFoundInsight)
    }

    @Test
    fun `getActionableInsights should return only actionable insights`() = runTest(testDispatcher) {
        // Given
        val actionableInsight = createTestInsight(actionable = true)
        val nonActionableInsight = createTestInsight(actionable = false)
        val testInsights = listOf(actionableInsight, nonActionableInsight)

        coEvery { insightsRepository.generateInsights(any(), any()) } returns testInsights

        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // When
        val actionableInsights = viewModel.getActionableInsights()

        // Then
        assertEquals(1, actionableInsights.size)
        assertEquals(actionableInsight, actionableInsights.first())
    }

    @Test
    fun `getFormattedImprovementPercentage should format correctly`() = runTest(testDispatcher) {
        // Given
        val positiveStats = createTestMoodStatistics().copy(improvementPercentage = 15.5)
        val negativeStats = createTestMoodStatistics().copy(improvementPercentage = -10.2)

        coEvery { insightsRepository.getMoodStatistics(any(), any()) } returns positiveStats

        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // When & Then
        assertEquals("+15.5%", viewModel.getFormattedImprovementPercentage())

        // Update with negative percentage
        coEvery { insightsRepository.getMoodStatistics(any(), any()) } returns negativeStats
        viewModel.refreshInsights()
        advanceUntilIdle()

        assertEquals("-10.2%", viewModel.getFormattedImprovementPercentage())
    }

    @Test
    fun `error handling should update error state`() = runTest(testDispatcher) {
        // Given
        coEvery { insightsRepository.generateInsights(any(), any()) } throws RuntimeException("Test error")

        // When
        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNotNull(uiState.errorMessage)
        assertTrue(uiState.errorMessage!!.contains("Test error"))
    }

    @Test
    fun `clearError should remove error message`() = runTest(testDispatcher) {
        // Given
        coEvery { insightsRepository.generateInsights(any(), any()) } throws RuntimeException("Test error")

        viewModel = InsightsViewModel(insightsRepository, getCurrentUserUseCase)
        advanceUntilIdle()

        // Verify error is set
        assertNotNull(viewModel.uiState.value.errorMessage)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.errorMessage)
    }

    // Helper methods
    private fun createTestUser(): User {
        return User(
            id = "test_user_1",
            name = "Test User",
            email = "test@example.com",
            joinDate = now.toLocalDate()
        )
    }

    private fun createTestMoodStatistics(): MoodStatistics {
        return MoodStatistics(
            totalEntries = 30,
            averageMood = 6.5,
            moodTrend = TrendDirection.IMPROVING,
            bestMood = 9,
            worstMood = 3,
            mostCommonEmotions = listOf("Happy", "Calm", "Motivated"),
            currentStreak = 7,
            longestStreak = 15,
            improvementPercentage = 12.5,
            consistencyScore = 0.85,
            period = "MONTH"
        )
    }

    private fun createTestInsight(
        type: InsightType = InsightType.MOOD_TREND,
        actionable: Boolean = false
    ): Insight {
        return Insight(
            id = "test_insight_${type.name.lowercase()}",
            type = type,
            title = "Test Insight",
            description = "This is a test insight",
            value = "7.5",
            trend = TrendDirection.IMPROVING,
            confidence = 0.8f,
            generatedAt = now,
            actionable = actionable,
            recommendation = if (actionable) "Take action!" else null
        )
    }

    private fun createTestEmotionAnalysis(): EmotionAnalysis {
        return EmotionAnalysis(
            emotion = "Happy",
            frequency = 15,
            percentage = 50.0,
            averageMoodWhenPresent = 7.5,
            trend = TrendDirection.IMPROVING
        )
    }

    private fun createTestActivityCorrelation(): ActivityCorrelation {
        return ActivityCorrelation(
            activity = "Exercise",
            occurrences = 10,
            averageMood = 8.0,
            moodImpact = MoodImpact.POSITIVE
        )
    }

    private fun createTestChartDataPoint(): ChartDataPoint {
        return ChartDataPoint(
            x = 0f,
            y = 7.5f,
            date = now,
            value = 7.5,
            label = "Happy"
        )
    }
}
