package com.steadymate.app.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.InsightsRepository
import com.steadymate.app.domain.repository.TimeRange
import com.steadymate.app.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Insights screen providing comprehensive mood analytics
 * Now with reactive flows and better state management
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _selectedTimeRange = MutableStateFlow(TimeRange.MONTH)
    private val _isRefreshing = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    // Reactive UI state with fallback data
    val uiState: StateFlow<InsightsUiState> = combine(
        _selectedTimeRange,
        _isRefreshing,
        _errorMessage
    ) { timeRange, isRefreshing, errorMessage ->
        try {
            val mockUserId = "mock_user_id" // Use fallback user ID for development
            InsightsUiState(
                selectedTimeRange = timeRange,
                isLoading = isRefreshing,
                errorMessage = errorMessage,
                insights = getInsights(mockUserId, timeRange),
                moodStatistics = getMoodStats(mockUserId, timeRange),
                emotionAnalysis = getEmotionAnalysis(mockUserId, timeRange),
                activityCorrelations = getActivityCorrelations(mockUserId, timeRange)
            )
        } catch (exception: Exception) {
            InsightsUiState(
                selectedTimeRange = timeRange,
                isLoading = false,
                errorMessage = exception.message ?: "An error occurred while loading insights"
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsUiState(isLoading = true)
    )
    
    // Chart data as separate flow for better performance
    val chartData: StateFlow<List<ChartDataPoint>> = _selectedTimeRange
        .map { timeRange ->
            try {
                val mockUserId = "mock_user_id"
                insightsRepository.getChartData(mockUserId, timeRange)
            } catch (e: Exception) {
                emptyList<ChartDataPoint>()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Mood trend data flow
    val moodTrendData: StateFlow<List<MoodTrendData>> = _selectedTimeRange
        .flatMapLatest { timeRange ->
            try {
                val mockUserId = "mock_user_id"
                insightsRepository.getMoodTrendDataFlow(mockUserId, timeRange)
            } catch (e: Exception) {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Suspend functions for data loading (used by reactive flows)
    private suspend fun getInsights(userId: String, timeRange: TimeRange): List<Insight> {
        return try {
            insightsRepository.generateInsights(userId, timeRange)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private suspend fun getMoodStats(userId: String, timeRange: TimeRange): MoodStatistics? {
        return try {
            insightsRepository.getMoodStatistics(userId, timeRange)
        } catch (e: Exception) {
            null
        }
    }
    
    private suspend fun getEmotionAnalysis(userId: String, timeRange: TimeRange): List<EmotionAnalysis> {
        return try {
            insightsRepository.analyzeEmotions(userId, timeRange)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private suspend fun getActivityCorrelations(userId: String, timeRange: TimeRange): List<ActivityCorrelation> {
        return try {
            insightsRepository.analyzeActivityCorrelations(userId, timeRange)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun refreshInsights() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            
            try {
                // Force refresh by emitting current time range
                val currentRange = _selectedTimeRange.value
                _selectedTimeRange.value = currentRange
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Refresh failed"
            } finally {
                _isRefreshing.value = false
            }
        }
    }
    
    fun selectTimeRange(range: TimeRange) {
        _selectedTimeRange.value = range
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Get specific insight by type
     */
    fun getInsightByType(type: InsightType): Insight? {
        return uiState.value.insights.firstOrNull { it.type == type }
    }
    
    /**
     * Get actionable insights only
     */
    fun getActionableInsights(): List<Insight> {
        return uiState.value.insights.filter { it.actionable }
    }
    
    /**
     * Get top emotions for current period
     */
    fun getTopEmotions(count: Int = 5): List<EmotionAnalysis> {
        return uiState.value.emotionAnalysis.take(count)
    }
    
    /**
     * Get mood improvement percentage formatted
     */
    fun getFormattedImprovementPercentage(): String {
        val percentage = uiState.value.moodStatistics?.improvementPercentage ?: 0.0
        return if (percentage > 0) "+${String.format("%.1f", percentage)}%" 
        else String.format("%.1f", percentage) + "%"
    }
}

/**
 * UI state for Insights screen
 */
data class InsightsUiState(
    val isLoading: Boolean = false,
    val insights: List<Insight> = emptyList(),
    val moodStatistics: MoodStatistics? = null,
    val emotionAnalysis: List<EmotionAnalysis> = emptyList(),
    val activityCorrelations: List<ActivityCorrelation> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.MONTH,
    val errorMessage: String? = null
)
