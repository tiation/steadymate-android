package com.steadymate.app.ui.screens.habits

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Comprehensive habits screen with creation, tracking, and statistics
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
                    HabitsList(
                        habits = uiState.habits,
                        onToggleCompletion = viewModel::toggleHabitCompletion,
                        onEditHabit = viewModel::startEditingHabit,
                        onDeleteHabit = { habitId ->
                            // Show confirmation before deleting
                            viewModel.deleteHabit(habitId)
                        }
                    )
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
                        text = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(), // Simplified format for now
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
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Habits Yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Start building positive routines by creating your first habit. Small, consistent actions lead to big changes!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddHabitClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Habit")
        }
    }
}

@Composable
private fun HabitsList(
    habits: List<HabitCompletion>,
    onToggleCompletion: (String) -> Unit,
    onEditHabit: (com.steadymate.app.domain.model.Habit) -> Unit,
    onDeleteHabit: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(habits) { habitCompletion ->
            HabitCard(
                habitCompletion = habitCompletion,
                onToggleCompletion = onToggleCompletion,
                onEditHabit = onEditHabit,
                onDeleteHabit = onDeleteHabit
            )
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
        modifier = Modifier
            .fillMaxWidth(),
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
        title = { Text("Create New Habit") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g., Drink 8 glasses of water") },
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
                    onCreateHabit(
                        title,
                        if (hasReminder && reminderTime.isNotBlank()) reminderTime else null
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create")
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
