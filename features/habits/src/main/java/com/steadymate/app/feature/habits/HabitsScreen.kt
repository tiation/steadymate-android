package com.steadymate.app.feature.habits

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.steadymate.app.core.ui.SteadyComponents
import com.steadymate.app.domain.model.*
import kotlinx.datetime.*
import kotlinx.datetime.format.*

/**
 * Main Habits screen showing today's habits, progress, and weekly overview
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showTemplateSheet by viewModel.showTemplateSheet.collectAsState()
    val selectedHabitForSkip by viewModel.selectedHabitForSkip.collectAsState()
    val haptics = LocalHapticFeedback.current

    // Template bottom sheet
    if (showTemplateSheet) {
        HabitTemplateBottomSheet(
            onTemplateSelected = { template ->
                viewModel.createHabitFromTemplate(template)
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            },
            onDismiss = { viewModel.hideTemplateSheet() }
        )
    }

    // Skip habit dialog
    selectedHabitForSkip?.let { habit ->
        SkipHabitDialog(
            habit = habit,
            onSkip = { reason -> viewModel.skipHabit(habit, reason) },
            onDismiss = { viewModel.hideSkipDialog() }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with daily progress
        item {
            DailyProgressCard(
                completedCount = uiState.completedTodayCount,
                totalCount = uiState.totalTodayCount,
                completionRate = uiState.todayCompletionRate,
                onAddHabit = { viewModel.showTemplateSheet() }
            )
        }

        // Quick stats
        if (uiState.todayHabits.isNotEmpty()) {
            item {
                QuickStatsRow(uiState.todayHabits)
            }
        }

        // Today's habits
        if (uiState.todayHabits.isNotEmpty()) {
            item {
                Text(
                    text = "Today's Habits",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.todayHabits) { habitState ->
                HabitCard(
                    habitState = habitState,
                    onComplete = { notes ->
                        viewModel.completeHabit(habitState, notes)
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    },
                    onSkip = { viewModel.showSkipDialog(habitState) }
                )
            }
        }

        // Weekly overview
        if (uiState.weeklyOverview.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                WeeklyOverviewCard(
                    weeklyData = uiState.weeklyOverview,
                    currentWeekStart = uiState.currentWeekStart,
                    onNavigateWeek = { offset -> viewModel.navigateToWeek(offset) }
                )
            }
        }

        // Empty state
        if (uiState.todayHabits.isEmpty() && !uiState.isLoading) {
            item {
                EmptyHabitsState(
                    onAddHabit = { viewModel.showTemplateSheet() }
                )
            }
        }

        // Error handling
        uiState.error?.let { error ->
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Loading indicator
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun DailyProgressCard(
    completedCount: Int,
    totalCount: Int,
    completionRate: Float,
    onAddHabit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$completedCount of $totalCount habits completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                FloatingActionButton(
                    onClick = onAddHabit,
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add habit",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { completionRate },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(completionRate * 100).toInt()}% complete",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickStatsRow(habits: List<HabitCompletionState>) {
    val currentStreak = habits.maxOfOrNull { it.stats.currentStreak } ?: 0
    val longestStreak = habits.maxOfOrNull { it.stats.longestStreak } ?: 0
    val averageCompletion = habits.map { it.stats.completionRate }.average().takeIf { !it.isNaN() } ?: 0.0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Current Streak",
            value = "$currentStreak",
            icon = "🔥",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Best Streak",
            value = "$longestStreak",
            icon = "⭐",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Avg Rate",
            value = "${(averageCompletion * 100).toInt()}%",
            icon = "📊",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitCard(
    habitState: HabitCompletionState,
    onComplete: (String) -> Unit,
    onSkip: () -> Unit
) {
    val habit = habitState.habit
    val stats = habitState.stats

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (habitState.isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = habit.icon,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .background(
                                Color(android.graphics.Color.parseColor(habit.color)).copy(alpha = 0.2f),
                                CircleShape
                            )
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (habit.description.isNotBlank()) {
                            Text(
                                text = habit.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Completion status
                if (habitState.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (habitState.isSkipped) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Skipped",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🔥 ${stats.currentStreak} day streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${stats.completionPercentage}% completion rate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Action buttons
            if (!habitState.isCompleted && !habitState.isSkipped) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SteadyComponents.SecondaryButton(
                        text = "Skip",
                        onClick = onSkip,
                        modifier = Modifier.weight(1f)
                    )
                    SteadyComponents.PrimaryButton(
                        text = "Complete",
                        onClick = { onComplete("") },
                        modifier = Modifier.weight(2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyOverviewCard(
    weeklyData: Map<LocalDate, List<HabitCompletionState>>,
    currentWeekStart: LocalDate,
    onNavigateWeek: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Week navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigateWeek(-1) }
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous week")
                }

                Text(
                    text = "Week of ${currentWeekStart.format(LocalDate.Format {
                        monthName(MonthNames.ENGLISH_ABBREVIATED)
                        char(' ')
                        dayOfMonth()
                    })}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                IconButton(
                    onClick = { onNavigateWeek(1) }
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next week")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Week grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val sortedDays = weeklyData.keys.sorted()
                items(sortedDays) { date ->
                    val dayHabits = weeklyData[date] ?: emptyList()
                    val completionRate = if (dayHabits.isNotEmpty()) {
                        dayHabits.count { it.isCompleted }.toFloat() / dayHabits.size
                    } else 0f

                    WeekDayCell(
                        date = date,
                        completionRate = completionRate,
                        habitCount = dayHabits.size
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDayCell(
    date: LocalDate,
    completionRate: Float,
    habitCount: Int
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today

    Card(
        modifier = Modifier
            .size(40.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isToday -> MaterialTheme.colorScheme.primary
                completionRate >= 1f -> MaterialTheme.colorScheme.primaryContainer
                completionRate >= 0.5f -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                habitCount > 0 -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = if (isToday) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun EmptyHabitsState(
    onAddHabit: () -> Unit
) {
    SteadyComponents.EmptyState(
        icon = "🎯",
        title = "No Habits Yet",
        description = "Start building healthy habits to improve your mental wellbeing. Choose from our curated templates or create your own.",
        actionText = "Browse Habit Templates",
        onAction = onAddHabit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkipHabitDialog(
    habit: HabitCompletionState,
    onSkip: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedReason by remember { mutableStateOf<SkipReason?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Skip ${habit.habit.name}?") },
        text = {
            Column {
                Text(
                    text = "Why are you skipping this habit today?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(SkipReason.entries) { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${reason.emoji} ${reason.displayName}")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    selectedReason?.let { reason ->
                        onSkip(reason.displayName)
                    }
                },
                enabled = selectedReason != null
            ) {
                Text("Skip Habit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitTemplateBottomSheet(
    onTemplateSelected: (HabitTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Choose a Habit Template",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(HabitTemplates.MENTAL_HEALTH_HABITS) { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTemplateSelected(template) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = template.icon,
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .background(
                                        Color(android.graphics.Color.parseColor(template.category.color)).copy(alpha = 0.2f),
                                        CircleShape
                                    )
                                    .padding(8.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = template.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = template.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = template.category.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(android.graphics.Color.parseColor(template.category.color)),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
