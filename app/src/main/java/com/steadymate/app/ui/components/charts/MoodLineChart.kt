package com.steadymate.app.ui.components.charts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.steadymate.app.domain.model.ChartDataPoint
import com.steadymate.app.domain.repository.TimeRange
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Beautiful mood trend line chart component using Vico
 * Shows mood data over time with proper accessibility support
 */
@Composable
fun MoodLineChart(
    chartData: List<ChartDataPoint>,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    
    // Convert data to Vico entries
    val chartEntries = remember(chartData) {
        chartData.mapIndexed { index, dataPoint ->
            FloatEntry(
                x = index.toFloat(),
                y = dataPoint.y
            )
        }
    }
    
    val chartEntryModel = remember(chartEntries) {
        ChartEntryModelProducer(chartEntries).getModel()
    }
    
    // Custom axis formatter for dates
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
        val index = value.toInt()
        if (index >= 0 && index < chartData.size) {
            chartData[index].label ?: ""
        } else ""
    }
    
    // Mood level formatter (0-10 scale)
    val startAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, chartValues ->
        value.toInt().toString()
    }
    
    // Accessibility description
    val accessibilityDescription = remember(chartData) {
        if (chartData.isEmpty()) {
            "Empty mood chart"
        } else {
            val avgMood = chartData.map { it.y }.average()
            val trend = if (chartData.size > 1) {
                val first = chartData.first().y
                val last = chartData.last().y
                when {
                    last > first + 0.5 -> "improving"
                    last < first - 0.5 -> "declining"
                    else -> "stable"
                }
            } else "stable"
            
            "Mood trend chart showing ${chartData.size} data points over $timeRange. " +
                "Average mood is ${String.format("%.1f", avgMood)} out of 10. " +
                "Trend is $trend."
        }
    }
    
    Column(
        modifier = modifier.semantics { 
            contentDescription = accessibilityDescription
        }
    ) {
        if (chartData.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No data available for this period",
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
                        chart = lineChart(),
                        model = chartEntryModel,
                        startAxis = startAxis(
                            valueFormatter = startAxisValueFormatter
                        ),
                        bottomAxis = bottomAxis(
                            valueFormatter = bottomAxisValueFormatter
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .height(250.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
            } else {
                // Fallback when chart model is null
                SimpleMoodChart(
                    chartData = chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp)
                )
            }
            
            // Chart legend/summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val avgMood = chartData.map { it.y }.average()
                val highestMood = chartData.maxOfOrNull { it.y } ?: 0f
                val lowestMood = chartData.minOfOrNull { it.y } ?: 0f
                
                ChartSummaryItem(
                    label = "Average",
                    value = String.format("%.1f", avgMood),
                    color = colorScheme.primary
                )
                
                ChartSummaryItem(
                    label = "Highest",
                    value = String.format("%.1f", highestMood),
                    color = colorScheme.tertiary
                )
                
                ChartSummaryItem(
                    label = "Lowest", 
                    value = String.format("%.1f", lowestMood),
                    color = colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun ChartSummaryItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Simple fallback chart using Canvas when Vico fails
 */
@Composable
private fun SimpleMoodChart(
    chartData: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    
    Box(
        modifier = modifier
            .background(surfaceColor.copy(alpha = 0.1f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (chartData.isNotEmpty()) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val path = Path()
                
                val maxY = chartData.maxOfOrNull { it.y } ?: 10f
                val minY = chartData.minOfOrNull { it.y } ?: 0f
                val range = maxY - minY
                
                chartData.forEachIndexed { index, point ->
                    val x = (index.toFloat() / (chartData.size - 1).coerceAtLeast(1)) * canvasWidth
                    val y = canvasHeight - ((point.y - minY) / range.coerceAtLeast(1f)) * canvasHeight
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        } else {
            Text(
                text = "📊\nChart data is loading...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
