package com.steadymate.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Material 3 mood slider with color-coded feedback and accessibility
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showLabels: Boolean = true,
    steps: Int = 9
) {
    val moodColor = getMoodColor(value)
    val moodEmoji = getMoodEmoji(value)
    val moodDescription = getMoodDescription(value)
    
    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                contentDescription = "Mood slider: $moodDescription"
            }
    ) {
        // Current mood display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = moodEmoji,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Column {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = moodColor,
                    fontWeight = FontWeight.Bold
                )
                
                if (showLabels && value > 0) {
                    Text(
                        text = moodDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = moodColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Slider
        androidx.compose.material3.Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            valueRange = 0f..10f,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = moodColor,
                activeTrackColor = moodColor.copy(alpha = 0.7f),
                inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
        
        // Labels
        if (showLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "😞 Very Low",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = "😊 Very High",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Tag chips for mood and other categorization
 */
@Composable
fun TagChips(
    tags: List<String>,
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(tags) { tag ->
            val isSelected = selectedTags.contains(tag)
            
            FilterChip(
                onClick = { if (enabled) onTagToggle(tag) },
                label = { 
                    Text(
                        text = tag,
                        fontSize = 14.sp
                    ) 
                },
                selected = isSelected,
                enabled = enabled,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    disabledSelectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.semantics {
                    contentDescription = if (isSelected) "$tag selected" else "$tag not selected"
                    role = Role.Checkbox
                }
            )
        }
    }
}

/**
 * Steady chart component using Vico for mood and habit tracking
 */
@Composable
fun SteadyChart(
    dataPoints: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    title: String? = null,
    chartType: ChartType = ChartType.LINE
) {
    Column(
        modifier = modifier
    ) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        if (dataPoints.isNotEmpty()) {
            when (chartType) {
                ChartType.LINE -> {
                    val entries = dataPoints.mapIndexed { index, dataPoint ->
                        FloatEntry(index.toFloat(), dataPoint.value)
                    }
                    
                    Chart(
                        chart = lineChart(
                            lines = listOf(
                                LineChart.LineSpec(
                                    lineColor = MaterialTheme.colorScheme.primary.toArgb(),
                                    lineBackgroundShader = null
                                )
                            )
                        ),
                        model = entryModelOf(entries),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .semantics {
                                contentDescription = "Chart showing ${dataPoints.size} data points"
                            }
                    )
                }
                
                ChartType.BAR -> {
                    // Simple custom bar chart for now
                    CustomBarChart(
                        dataPoints = dataPoints,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "No data yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Primary button with consistent styling and accessibility
 */
@Composable
fun SteadyPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics {
                if (loading) {
                    contentDescription = "Loading, please wait"
                }
            },
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        content()
    }
}

/**
 * Secondary button with outline styling
 */
@Composable
fun SteadySecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        border = BorderStroke(
            1.dp,
            if (enabled) MaterialTheme.colorScheme.outline 
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        content()
    }
}

/**
 * Form field with consistent styling and error handling
 */
@Composable
fun SteadyFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    maxLines: Int = 1,
    singleLine: Boolean = maxLines == 1
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    if (isError && errorMessage != null) {
                        error(errorMessage)
                    }
                },
            enabled = enabled,
            isError = isError,
            maxLines = maxLines,
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Animated loading indicator for data states
 */
@Composable
fun SteadyLoadingIndicator(
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics {
                contentDescription = text ?: "Loading"
            }
        )
        
        if (text != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Empty state component with illustration and action
 */
@Composable
fun SteadyEmptyState(
    title: String,
    subtitle: String,
    emoji: String = "🌱",
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 48.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            SteadyPrimaryButton(
                onClick = onActionClick
            ) {
                Text(actionText)
            }
        }
    }
}

// Helper functions
@Composable
private fun getMoodColor(mood: Int): Color {
    return when (mood) {
        in 0..2 -> Color(0xFFD32F2F) // Red
        in 3..4 -> Color(0xFFFF9800) // Orange
        in 5..6 -> Color(0xFFFFC107) // Amber
        in 7..8 -> Color(0xFF8BC34A) // Light Green
        in 9..10 -> Color(0xFF4CAF50) // Green
        else -> MaterialTheme.colorScheme.outline
    }
}

private fun getMoodEmoji(mood: Int): String {
    return when (mood) {
        0 -> "😶"
        in 1..2 -> "😞"
        in 3..4 -> "😕"
        in 5..6 -> "😐"
        in 7..8 -> "🙂"
        in 9..10 -> "😊"
        else -> "😶"
    }
}

private fun getMoodDescription(mood: Int): String {
    return when (mood) {
        0 -> "Not selected"
        1 -> "Very Low"
        2 -> "Low"
        3 -> "Below Average"
        4 -> "Somewhat Low"
        5 -> "Neutral"
        6 -> "Somewhat Good"
        7 -> "Good"
        8 -> "Very Good"
        9 -> "Excellent"
        10 -> "Perfect"
        else -> "Select mood"
    }
}

@Composable
private fun CustomBarChart(
    dataPoints: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    val maxValue = dataPoints.maxOfOrNull { it.value } ?: 1f
    
    Canvas(modifier = modifier) {
        val barWidth = size.width / (dataPoints.size * 1.5f)
        val barSpacing = barWidth * 0.5f
        
        dataPoints.forEachIndexed { index, dataPoint ->
            val barHeight = (dataPoint.value / maxValue) * size.height * 0.8f
            val barX = index * (barWidth + barSpacing) + barSpacing
            val barY = size.height - barHeight
            
            drawRect(
                color = Color(0xFF6200EA),
                topLeft = Offset(barX, barY),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
        }
    }
}

// Data classes
data class ChartDataPoint(
    val label: String,
    val value: Float,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChartType {
    LINE,
    BAR
}

// Preview functions
@Preview(showBackground = true)
@Composable
private fun MoodSliderPreview() {
    MaterialTheme {
        var mood by remember { mutableIntStateOf(7) }
        MoodSlider(
            value = mood,
            onValueChange = { mood = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagChipsPreview() {
    MaterialTheme {
        var selectedTags by remember { mutableStateOf(listOf("work", "family")) }
        TagChips(
            tags = listOf("work", "family", "exercise", "sleep", "social", "health"),
            selectedTags = selectedTags,
            onTagToggle = { tag ->
                selectedTags = if (selectedTags.contains(tag)) {
                    selectedTags - tag
                } else {
                    selectedTags + tag
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SteadyChartPreview() {
    MaterialTheme {
        val sampleData = listOf(
            ChartDataPoint("Mon", 6f),
            ChartDataPoint("Tue", 8f),
            ChartDataPoint("Wed", 5f),
            ChartDataPoint("Thu", 9f),
            ChartDataPoint("Fri", 7f)
        )
        
        SteadyChart(
            dataPoints = sampleData,
            title = "Weekly Mood",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SteadyEmptyStatePreview() {
    MaterialTheme {
        SteadyEmptyState(
            title = "No habits yet",
            subtitle = "Start building positive daily habits for better mental health",
            emoji = "🌱",
            actionText = "Add your first habit",
            onActionClick = { }
        )
    }
}
