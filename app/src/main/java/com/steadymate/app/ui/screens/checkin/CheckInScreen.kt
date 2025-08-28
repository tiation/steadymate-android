package com.steadymate.app.ui.screens.checkin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    onNavigateBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chartData by viewModel.chartData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with streak info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.hasCheckedInToday) {
                    Text(
                        text = "✅ Already checked in today!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${uiState.currentStreak} day streak",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = "Daily Check-In",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Current streak: ${uiState.currentStreak} days",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                // Success animation
                AnimatedVisibility(
                    visible = uiState.isSubmitted,
                    enter = scaleIn(animationSpec = tween(500)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "🎉 Great job!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Check-in completed successfully!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        if (!uiState.hasCheckedInToday && !uiState.isSubmitted) {
            // Mood Level Slider
            Card {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "How are you feeling today?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = getMoodDescription(uiState.moodLevel),
                        style = MaterialTheme.typography.bodyMedium,
                        color = getMoodColor(uiState.moodLevel)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Slider(
                        value = uiState.moodLevel.toFloat(),
                        onValueChange = { viewModel.updateMoodLevel(it.roundToInt()) },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = getMoodColor(uiState.moodLevel),
                            activeTrackColor = getMoodColor(uiState.moodLevel).copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
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

            // Emotion Tags
            Card {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Select emotions (tap to toggle)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(uiState.availableTags) { tag ->
                            FilterChip(
                                onClick = { viewModel.toggleTag(tag) },
                                label = { Text(tag) },
                                selected = uiState.selectedTags.contains(tag),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    if (uiState.selectedTags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selected: ${uiState.selectedTags.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Notes
            Card {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Notes (optional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::updateNotes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("What's on your mind today?") },
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // Submit Button
            val submitButtonAlpha by animateFloatAsState(
                targetValue = if (uiState.isValidInput) 1f else 0.5f,
                label = "submit_button_alpha"
            )

            Button(
                onClick = viewModel::submitCheckIn,
                enabled = uiState.isValidInput && !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(submitButtonAlpha),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = if (uiState.isSubmitting) "Saving..." else "Complete Check-In",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Charts Section
        if (chartData.isNotEmpty()) {
            Card {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mood Trends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        // Period selector
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ChartPeriod.values().forEach { period ->
                                FilterChip(
                                    onClick = { viewModel.updateChartPeriod(period) },
                                    label = { 
                                        Text(
                                            text = when (period) {
                                                ChartPeriod.WEEK -> "7D"
                                                ChartPeriod.MONTH -> "30D"
                                                ChartPeriod.THREE_MONTHS -> "90D"
                                            },
                                            fontSize = 12.sp
                                        )
                                    },
                                    selected = uiState.selectedChartPeriod == period,
                                    modifier = Modifier.height(32.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chart
                    if (chartData.isNotEmpty()) {
                        val entries = chartData.mapIndexed { index, dataPoint ->
                            FloatEntry(index.toFloat(), dataPoint.moodLevel.toFloat())
                        }
                        
                        Chart(
                            chart = lineChart(
                                lines = listOf(
                                    // Line chart styling can be customized here
                                )
                            ),
                            model = entryModelOf(entries),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Track your mood patterns over time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            viewModel.clearError()
        }
    }

    // Success handling with haptic feedback
    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            // Add a slight delay before potentially navigating
            kotlinx.coroutines.delay(2000)
            // You can uncomment the next line if you want to navigate back automatically
            // onNavigateBack()
        }
    }
}

@Composable
private fun getMoodColor(level: Int): Color {
    return when (level) {
        in 0..2 -> Color(0xFFD32F2F) // Red
        in 3..4 -> Color(0xFFFF9800) // Orange  
        in 5..6 -> Color(0xFFFFC107) // Amber
        in 7..8 -> Color(0xFF8BC34A) // Light Green
        in 9..10 -> Color(0xFF4CAF50) // Green
        else -> Color.Gray
    }
}

@Composable
private fun getMoodDescription(level: Int): String {
    return when (level) {
        0 -> "Select your mood level"
        1 -> "Very Low - Feeling terrible"
        2 -> "Low - Having a tough time"
        3 -> "Below Average - Struggling a bit"
        4 -> "Somewhat Low - Not great"
        5 -> "Neutral - Okay"
        6 -> "Somewhat Good - Doing alright"
        7 -> "Good - Feeling positive"
        8 -> "Very Good - Having a great day"
        9 -> "Excellent - Feeling amazing"
        10 -> "Perfect - On top of the world!"
        else -> "Select your mood level"
    }
}
