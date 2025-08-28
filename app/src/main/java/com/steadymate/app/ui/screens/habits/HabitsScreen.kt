package com.steadymate.app.ui.screens.habits

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Enhanced Habits screen with habit stacking encouragement and comprehensive tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle error messages
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showAddHabitDialog,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Habit",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            HabitsHeader(
                todayCompletedCount = uiState.habits.count { it.isCompletedToday },
                totalHabitsCount = uiState.habits.size,
                onAddHabitClick = viewModel::showAddHabitDialog
            )
            
            // Content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading your habits...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                uiState.habits.isEmpty() && !uiState.isLoading -> {
                    EmptyHabitsState(
                        onAddHabitClick = viewModel::showAddHabitDialog
                    )
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Habit stacking encouragement for users with 1+ habits
                        if (uiState.habits.size in 1..4) {
                            item {
                                HabitStackingEncouragementCard(
                                    existingHabits = uiState.habits,
                                    onAddHabitClick = viewModel::showAddHabitDialog
                                )
                            }
                        }
                        
                        // Habit list
                        items(uiState.habits) { habitCompletion ->
                            HabitCard(
                                habitCompletion = habitCompletion,
                                onToggleCompletion = viewModel::toggleHabitCompletion,
                                onEditHabit = viewModel::startEditingHabit,
                                onDeleteHabit = { habitId ->
                                    viewModel.deleteHabit(habitId)
                                }
                            )
                        }
                        
                        // Success celebration for established users
                        if (uiState.habits.size >= 5) {
                            item {
                                HabitMasteryCard(habits = uiState.habits)
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Dialogs
    if (uiState.showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = viewModel::hideAddHabitDialog,
            onCreateHabit = { title, reminderTime ->
                viewModel.createHabit(title, reminderTime)
            }
        )
    }
    
    uiState.editingHabit?.let { habit ->
        EditHabitDialog(
            habit = habit,
            onDismiss = viewModel::cancelEditingHabit,
            onUpdateHabit = viewModel::updateHabit
        )
    }
}

@Composable
private fun HabitsHeader(
    todayCompletedCount: Int,
    totalHabitsCount: Int,
    onAddHabitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Habits",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onAddHabitClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Habit",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Today's Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$todayCompletedCount/$totalHabitsCount",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (totalHabitsCount > 0) {
                        val progress = todayCompletedCount.toFloat() / totalHabitsCount.toFloat()
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHabitsState(
    onAddHabitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🌱",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Ready to build better habits?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Start small, think big. Every great journey begins with a single step.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Button(
            onClick = onAddHabitClick,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add My First Habit")
        }
    }
}

// This card is now removed to simplify the interface

@Composable
private fun HabitStackingEncouragementCard(
    existingHabits: List<HabitCompletion>,
    onAddHabitClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "✨ You're doing great!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Want to add another habit? The best time is right after something you already do.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            OutlinedButton(
                onClick = onAddHabitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Another Habit")
            }
        }
    }
}

@Composable
private fun HabitMasteryCard(
    habits: List<HabitCompletion>
) {
    val totalCompleted = habits.count { it.isCompletedToday }
    val bestStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎉 Amazing progress!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "You've built ${habits.size} habits. Keep up the great work!",
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (bestStreak > 0) {
                Text(
                    text = "🔥 Best streak: $bestStreak days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitCard(
    habitCompletion: HabitCompletion,
    onToggleCompletion: (String) -> Unit,
    onEditHabit: (com.steadymate.app.domain.model.Habit) -> Unit,
    onDeleteHabit: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onToggleCompletion(habitCompletion.habit.id) },
        colors = CardDefaults.cardColors(
            containerColor = if (habitCompletion.isCompletedToday) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (habitCompletion.isCompletedToday) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (habitCompletion.isCompletedToday) 2.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion Indicator
            val circleColor by animateColorAsState(
                targetValue = if (habitCompletion.isCompletedToday) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(circleColor),
                contentAlignment = Alignment.Center
            ) {
                if (habitCompletion.isCompletedToday) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Habit Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = habitCompletion.habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (habitCompletion.currentStreak > 0) {
                        Text(
                            text = "🔥 ${habitCompletion.currentStreak} day streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Text(
                        text = "${(habitCompletion.completionRate * 100).toInt()}% complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                habitCompletion.habit.reminderTime?.let { time ->
                    Text(
                        text = "⏰ $time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Menu Button
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            onEditHabit(habitCompletion.habit)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            onDeleteHabit(habitCompletion.habit.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onCreateHabit: (String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }
    var hasReminder by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Add New Habit 🌱",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What's your new habit?") },
                    placeholder = { Text("e.g., Drink a glass of water") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        Text(
                            text = "Keep it simple and specific",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remind me daily")
                }
                
                if (hasReminder) {
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = { Text("When?") },
                        placeholder = { Text("e.g., 9:00 AM") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateHabit(
                        title,
                        if (hasReminder && reminderTime.isNotBlank()) reminderTime else null
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add Habit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditHabitDialog(
    habit: com.steadymate.app.domain.model.Habit,
    onDismiss: () -> Unit,
    onUpdateHabit: (String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf(habit.title) }
    var reminderTime by remember { mutableStateOf(habit.reminderTime ?: "") }
    var hasReminder by remember { mutableStateOf(habit.reminderTime != null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Habit") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set daily reminder")
                }
                
                if (hasReminder) {
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = { Text("Reminder Time") },
                        placeholder = { Text("e.g., 09:00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdateHabit(
                        habit.id,
                        title,
                        if (hasReminder && reminderTime.isNotBlank()) reminderTime else null
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
