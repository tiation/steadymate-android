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
