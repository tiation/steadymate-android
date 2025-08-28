package com.steadymate.app.ui.components.charts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.steadymate.app.domain.model.EmotionAnalysis
import androidx.compose.ui.graphics.Brush
import kotlin.math.max

/**
 * Beautiful emotion analysis bar chart component using Vico
 * Shows emotion frequencies with mood correlation colors
 */
@Composable
fun EmotionBarChart(
    emotions: List<EmotionAnalysis>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    
    // Convert data to Vico entries
    val chartEntries = remember(emotions) {
        emotions.take(5).mapIndexed { index, emotion ->
            FloatEntry(
                x = index.toFloat(),
                y = emotion.frequency.toFloat()
            )
        }
    }
    
    val chartEntryModel = remember(chartEntries) {
        ChartEntryModelProducer(chartEntries).getModel()
    }
    
    // Custom axis formatter for emotion names
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
        val index = value.toInt()
        if (index >= 0 && index < emotions.size) {
            emotions[index].emotion.take(8) // Truncate long emotion names
        } else ""
    }
    
    // Frequency formatter
    val startAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, chartValues ->
        value.toInt().toString()
    }
    
    // Generate colors based on mood impact
    val emotionColors = remember(emotions) {
        emotions.take(5).map { emotion ->
            getEmotionColor(emotion, colorScheme)
        }
    }
    
    // Accessibility description
    val accessibilityDescription = remember(emotions) {
        if (emotions.isEmpty()) {
            "Empty emotion chart"
        } else {
            val topEmotion = emotions.first()
            "Emotion frequency chart showing top ${emotions.size} emotions. " +
                "Most frequent emotion is ${topEmotion.emotion} with ${topEmotion.frequency} occurrences, " +
                "representing ${topEmotion.percentage.toInt()}% of entries."
        }
    }
    
    Column(
        modifier = modifier.semantics { 
            contentDescription = accessibilityDescription
        }
    ) {
        if (emotions.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No emotion data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Chart
            if (chartEntryModel != null) {
                ProvideChartStyle {
                    Chart(
                        chart = columnChart(),
                        model = chartEntryModel,
                        startAxis = startAxis(
                            valueFormatter = startAxisValueFormatter
                        ),
                        bottomAxis = bottomAxis(
                            valueFormatter = bottomAxisValueFormatter
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            
            // Emotion details list
            Spacer(modifier = Modifier.height(16.dp))
            
            emotions.take(3).forEachIndexed { index, emotion ->
                EmotionDetailItem(
                    emotion = emotion,
                    color = emotionColors.getOrNull(index) ?: colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EmotionDetailItem(
    emotion: EmotionAnalysis,
    color: Color,
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
                color = color
            )
            Text(
                text = "${emotion.frequency} times • ${emotion.percentage.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = String.format("%.1f", emotion.averageMoodWhenPresent),
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
            Text(
                text = "avg mood",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getEmotionColor(emotion: EmotionAnalysis, colorScheme: androidx.compose.material3.ColorScheme): Color {
    // Color emotions based on their average mood and type
    val emotionName = emotion.emotion.lowercase()
    val averageMood = emotion.averageMoodWhenPresent
    
    return when {
        // Positive emotions - use warm colors
        emotionName in listOf("happy", "joy", "excited", "content", "grateful", "peaceful") -> {
            if (averageMood >= 7) colorScheme.primary
            else Color(0xFF4CAF50) // Green
        }
        
        // Neutral emotions - use neutral colors
        emotionName in listOf("calm", "focused", "tired", "bored") -> {
            colorScheme.tertiary
        }
        
        // Negative emotions - use cooler colors
        emotionName in listOf("sad", "anxious", "angry", "stressed", "frustrated", "worried") -> {
            when {
                averageMood <= 3 -> Color(0xFFF44336) // Red
                averageMood <= 5 -> Color(0xFFFF9800) // Orange
                else -> Color(0xFFFFEB3B) // Yellow
            }
        }
        
        // Default based on average mood
        else -> {
            when {
                averageMood >= 7 -> colorScheme.primary
                averageMood >= 5 -> colorScheme.secondary
                averageMood >= 3 -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            }
        }
    }
}
