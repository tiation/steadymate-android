package com.steadymate.app.ui.screens.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steadymate.app.domain.model.*
import com.steadymate.app.domain.repository.TimeRange
import com.steadymate.app.ui.components.charts.MoodLineChart
import com.steadymate.app.ui.components.charts.EmotionBarChart
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chartData by viewModel.chartData.collectAsStateWithLifecycle()
    val moodTrendData by viewModel.moodTrendData.collectAsStateWithLifecycle()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.errorMessage != null -> {
                ErrorState(
                    error = uiState.errorMessage!!,
                    onRetry = { viewModel.refreshInsights() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            else -> {
                InsightsContent(
                    uiState = uiState,
                    chartData = chartData,
                    moodTrendData = moodTrendData,
                    onTimeRangeSelected = viewModel::selectTimeRange,
                    onRefresh = viewModel::refreshInsights,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

@Composable
private fun InsightsContent(
    uiState: InsightsUiState,
    chartData: List<ChartDataPoint>,
    moodTrendData: List<MoodTrendData>,
    onTimeRangeSelected: (TimeRange) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with time range selector
        item {
            InsightsHeader(
                selectedTimeRange = uiState.selectedTimeRange,
                onTimeRangeSelected = onTimeRangeSelected,
                onRefresh = onRefresh
            )
        }
        
        // Statistics overview cards
        item {
            uiState.moodStatistics?.let { stats ->
                StatisticsOverview(statistics = stats)
            }
        }
        
        // Mood trend chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Mood Trend",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    AnimatedVisibility(
                        visible = chartData.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        MoodLineChart(
                            chartData = chartData,
                            timeRange = uiState.selectedTimeRange
                        )
                    }
                }
            }
        }
        
        // Key insights
        item {
            if (uiState.insights.isNotEmpty()) {
                KeyInsights(insights = uiState.insights.take(3))
            }
        }
        
        // Emotion analysis
        item {
            if (uiState.emotionAnalysis.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Emotion Analysis",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        EmotionBarChart(emotions = uiState.emotionAnalysis)
                    }
                }
            }
        }
        
        // Activity correlations
        item {
            if (uiState.activityCorrelations.isNotEmpty()) {
                ActivityCorrelationsSection(activities = uiState.activityCorrelations.take(5))
            }
        }
    }
}

@Composable
private fun InsightsHeader(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh insights"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time range selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TimeRange.values()) { timeRange ->
                    TimeRangeChip(
                        timeRange = timeRange,
                        isSelected = timeRange == selectedTimeRange,
                        onClick = { onTimeRangeSelected(timeRange) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeRangeChip(
    timeRange: TimeRange,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = when (timeRange) {
                    TimeRange.WEEK -> "Week"
                    TimeRange.MONTH -> "Month"
                    TimeRange.THREE_MONTHS -> "3 Months"
                    TimeRange.SIX_MONTHS -> "6 Months"
                    TimeRange.YEAR -> "Year"
                }
            )
        },
        modifier = modifier
    )
}

@Composable
private fun StatisticsOverview(
    statistics: MoodStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Average Mood",
                    value = String.format("%.1f", statistics.averageMood),
                    subtitle = "out of 10",
                    icon = Icons.Default.Face,
                    color = getTrendColor(statistics.moodTrend)
                )
                
                StatCard(
                    title = "Current Streak",
                    value = statistics.currentStreak.toString(),
                    subtitle = "days",
                    icon = Icons.Default.Star,
                    color = MaterialTheme.colorScheme.primary
                )
                
                StatCard(
                    title = "Total Entries",
                    value = statistics.totalEntries.toString(),
                    subtitle = "this ${statistics.period.lowercase()}",
                    icon = Icons.Default.List,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MoodTrendChart(
    chartData: List<ChartDataPoint>,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mood Trend",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple chart placeholder - will be replaced with proper chart later
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (chartData.isNotEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Chart placeholder",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${chartData.size} data points",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Average: ${String.format("%.1f", chartData.map { it.y }.average())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "No data available for this period",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyInsights(
    insights: List<Insight>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Key Insights",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            insights.forEach { insight ->
                InsightCard(
                    insight = insight,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (insight != insights.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun InsightCard(
    insight: Insight,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = getInsightColor(insight.type).copy(alpha = 0.1f)
        ),
        border = if (insight.actionable) {
            CardDefaults.outlinedCardBorder()
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                
                TrendIcon(trend = insight.trend)
            }
            
            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            if (insight.recommendation != null) {
                Text(
                    text = "💡 ${insight.recommendation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EmotionAnalysisSection(
    emotions: List<EmotionAnalysis>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Top Emotions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            emotions.forEach { emotion ->
                EmotionItem(
                    emotion = emotion,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (emotion != emotions.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun EmotionItem(
    emotion: EmotionAnalysis,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = emotion.emotion,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${emotion.frequency} times (${emotion.percentage.roundToInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%.1f", emotion.averageMoodWhenPresent),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            TrendIcon(trend = emotion.trend)
        }
    }
}

@Composable
private fun ActivityCorrelationsSection(
    activities: List<ActivityCorrelation>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Activity Impact",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            activities.forEach { activity ->
                ActivityItem(
                    activity = activity,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (activity != activities.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(
    activity: ActivityCorrelation,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = activity.activity,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${activity.occurrences} times",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%.1f", activity.averageMood),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            MoodImpactIcon(impact = activity.moodImpact)
        }
    }
}

@Composable
private fun TrendIcon(
    trend: TrendDirection,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (trend) {
        TrendDirection.IMPROVING -> Icons.Default.KeyboardArrowUp to Color(0xFF4CAF50)
        TrendDirection.DECLINING -> Icons.Default.KeyboardArrowDown to Color(0xFFF44336)
        TrendDirection.STABLE -> Icons.Default.ArrowForward to MaterialTheme.colorScheme.onSurfaceVariant
        TrendDirection.FLUCTUATING -> Icons.Default.KeyboardArrowUp to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Icon(
        imageVector = icon,
        contentDescription = trend.name,
        tint = color,
        modifier = modifier.size(20.dp)
    )
}

@Composable
private fun MoodImpactIcon(
    impact: MoodImpact,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (impact) {
        MoodImpact.VERY_POSITIVE -> Icons.Default.ThumbUp to Color(0xFF4CAF50)
        MoodImpact.POSITIVE -> Icons.Default.ThumbUp to Color(0xFF8BC34A)
        MoodImpact.NEUTRAL -> Icons.Default.ArrowForward to MaterialTheme.colorScheme.onSurfaceVariant
        MoodImpact.NEGATIVE -> Icons.Default.KeyboardArrowDown to Color(0xFFFF9800)
        MoodImpact.VERY_NEGATIVE -> Icons.Default.KeyboardArrowDown to Color(0xFFF44336)
    }
    
    Icon(
        imageVector = icon,
        contentDescription = impact.name,
        tint = color,
        modifier = modifier.size(20.dp)
    )
}

@Composable
private fun getTrendColor(trend: TrendDirection): Color {
    return when (trend) {
        TrendDirection.IMPROVING -> Color(0xFF4CAF50)
        TrendDirection.DECLINING -> Color(0xFFF44336)
        TrendDirection.STABLE -> MaterialTheme.colorScheme.primary
        TrendDirection.FLUCTUATING -> Color(0xFFFF9800)
    }
}

@Composable
private fun getInsightColor(type: InsightType): Color {
    return when (type) {
        InsightType.MOOD_TREND -> MaterialTheme.colorScheme.primary
        InsightType.EMOTION_PATTERN -> Color(0xFF9C27B0)
        InsightType.ACTIVITY_CORRELATION -> Color(0xFF2196F3)
        InsightType.STREAK_ANALYSIS -> Color(0xFFFF5722)
        InsightType.IMPROVEMENT_OPPORTUNITY -> Color(0xFFFF9800)
        InsightType.POSITIVE_PATTERN -> Color(0xFF4CAF50)
        InsightType.WARNING_SIGN -> Color(0xFFF44336)
    }
}
