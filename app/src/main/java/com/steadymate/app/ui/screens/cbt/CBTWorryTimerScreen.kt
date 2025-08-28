package com.steadymate.app.ui.screens.cbt

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

/**
 * CBT Worry Timer Screen - Guided worry time sessions to contain and process worries
 * Uses structured worry periods followed by action planning to reduce rumination
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CBTWorryTimerScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CBTWorryTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Worry Timer") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (uiState.timerState != WorryTimerState.NOT_STARTED) {
                    IconButton(onClick = viewModel::resetSession) {
                        Icon(
                        imageVector = Icons.Default.Close,
                            contentDescription = "Stop Session"
                        )
                    }
                }
            }
        )
        
        // Content
        when (uiState.currentScreen) {
            WorryTimerScreen.SETUP -> {
                WorryTimerSetupScreen(
                    selectedDuration = uiState.selectedDuration,
                    onDurationSelected = viewModel::setDuration,
                    onStartTimer = viewModel::startWorryTimer
                )
            }
            WorryTimerScreen.ACTIVE_WORRYING -> {
                ActiveWorryScreen(
                    timeRemaining = uiState.timeRemaining,
                    timerState = uiState.timerState,
                    worries = uiState.currentWorries,
                    onAddWorry = viewModel::addWorry,
                    onPauseResume = viewModel::toggleTimer,
                    onComplete = viewModel::completeWorryTime
                )
            }
            WorryTimerScreen.ACTION_PLANNING -> {
                ActionPlanningScreen(
                    worries = uiState.currentWorries,
                    actionPlans = uiState.actionPlans,
                    onAddActionPlan = viewModel::addActionPlan,
                    onComplete = {
                        viewModel.saveSession()
                        onNavigateBack()
                    }
                )
            }
        }
    }
}

@Composable
private fun WorryTimerSetupScreen(
    selectedDuration: Int,
    onDurationSelected: (Int) -> Unit,
    onStartTimer: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Worry Timer",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "Set aside dedicated time for your worries, then create action plans",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        item {
            Text(
                text = "How it works:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        item {
            WorryStepCard(
                stepNumber = 1,
                title = "Set your worry time",
                description = "Choose how long you want to dedicate to worrying (10-30 minutes recommended)"
            )
        }
        
        item {
            WorryStepCard(
                stepNumber = 2,
                title = "Focus on your worries",
                description = "During this time, allow yourself to fully experience and write down your worries"
            )
        }
        
        item {
            WorryStepCard(
                stepNumber = 3,
                title = "Create action plans",
                description = "For each worry, identify if it's actionable and create concrete next steps"
            )
        }
        
        item {
            Text(
                text = "Choose your worry time:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            val durations = listOf(10, 15, 20, 30)
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(durations.size) { index ->
                    val duration = durations[index]
                    val isSelected = duration == selectedDuration
                    
                    FilterChip(
                        onClick = { onDurationSelected(duration) },
                        label = { Text("$duration min") },
                        selected = isSelected,
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onStartTimer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Start Worry Timer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun WorryStepCard(
    stepNumber: Int,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ActiveWorryScreen(
    timeRemaining: Long,
    timerState: WorryTimerState,
    worries: List<String>,
    onAddWorry: (String) -> Unit,
    onPauseResume: () -> Unit,
    onComplete: () -> Unit
) {
    var newWorryText by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TimerDisplay(
                timeRemaining = timeRemaining,
                timerState = timerState,
                onPauseResume = onPauseResume,
                onComplete = onComplete
            )
        }
        
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Add your worries:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = newWorryText,
                        onValueChange = { newWorryText = it },
                        label = { Text("What's worrying you?") },
                        placeholder = { Text("I'm worried that...") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (newWorryText.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        onAddWorry(newWorryText)
                                        newWorryText = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add worry"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
        
        if (worries.isNotEmpty()) {
            item {
                Text(
                    text = "Your current worries:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(worries.size) { index ->
                WorryCard(worry = worries[index], index = index + 1)
            }
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Remember:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "This is your designated worry time. Allow yourself to fully experience these concerns without judgment. After the timer ends, we'll work on action plans.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerDisplay(
    timeRemaining: Long,
    timerState: WorryTimerState,
    onPauseResume: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val minutes = timeRemaining / 60000
            val seconds = (timeRemaining % 60000) / 1000
            val timeText = String.format("%02d:%02d", minutes, seconds)
            
            Text(
                text = timeText,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (timerState) {
                    WorryTimerState.RUNNING -> {
                        Button(
                            onClick = onPauseResume,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pause")
                        }
                    }
                    WorryTimerState.PAUSED -> {
                        Button(onClick = onPauseResume) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Resume")
                        }
                    }
                    else -> { /* Handle other states */ }
                }
                
                OutlinedButton(onClick = onComplete) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("End Early")
                }
            }
        }
    }
}

@Composable
private fun WorryCard(
    worry: String,
    index: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = worry,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionPlanningScreen(
    worries: List<String>,
    actionPlans: Map<String, ActionPlan>,
    onAddActionPlan: (String, ActionPlan) -> Unit,
    onComplete: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Create Action Plans",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "For each worry, decide if it's actionable and create concrete next steps.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        items(worries.size) { index ->
            val worry = worries[index]
            ActionPlanCard(
                worry = worry,
                index = index + 1,
                actionPlan = actionPlans[worry],
                onSaveActionPlan = { plan -> onAddActionPlan(worry, plan) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Complete Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionPlanCard(
    worry: String,
    index: Int,
    actionPlan: ActionPlan?,
    onSaveActionPlan: (ActionPlan) -> Unit
) {
    var isActionable by remember { mutableStateOf(actionPlan?.isActionable ?: true) }
    var actionText by remember { mutableStateOf(actionPlan?.action ?: "") }
    var isExpanded by remember { mutableStateOf(actionPlan == null) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = index.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = worry,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (actionPlan != null && !isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                            imageVector = if (actionPlan.isActionable) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (actionPlan.isActionable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = if (actionPlan.isActionable) "Actionable" else "Not actionable",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (actionPlan.isActionable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        
                        if (actionPlan.isActionable && actionPlan.action.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = actionPlan.action,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                IconButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Is this worry actionable?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { isActionable = true },
                        label = { Text("Yes, actionable") },
                        selected = isActionable,
                        leadingIcon = if (isActionable) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                    
                    FilterChip(
                        onClick = { isActionable = false },
                        label = { Text("No, not actionable") },
                        selected = !isActionable,
                        leadingIcon = if (!isActionable) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
                
                if (isActionable) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = actionText,
                        onValueChange = { actionText = it },
                        label = { Text("What action can you take?") },
                        placeholder = { Text("I will...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { isExpanded = false }
                    ) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            onSaveActionPlan(ActionPlan(isActionable, actionText))
                            isExpanded = false
                        },
                        enabled = !isActionable || actionText.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// Data classes and enums
enum class WorryTimerState {
    NOT_STARTED,
    RUNNING,
    PAUSED,
    COMPLETED
}

enum class WorryTimerScreen {
    SETUP,
    ACTIVE_WORRYING,
    ACTION_PLANNING
}

data class ActionPlan(
    val isActionable: Boolean,
    val action: String
)

data class WorryTimerUiState(
    val currentScreen: WorryTimerScreen = WorryTimerScreen.SETUP,
    val selectedDuration: Int = 15, // minutes
    val timerState: WorryTimerState = WorryTimerState.NOT_STARTED,
    val timeRemaining: Long = 0L, // milliseconds
    val currentWorries: List<String> = emptyList(),
    val actionPlans: Map<String, ActionPlan> = emptyMap()
)

// Mock ViewModel (you'd implement the actual ViewModel)
class CBTWorryTimerViewModel : androidx.lifecycle.ViewModel() {
    private val _uiState = mutableStateOf(WorryTimerUiState())
    val uiState: State<WorryTimerUiState> = _uiState
    
    fun setDuration(minutes: Int) {
        _uiState.value = _uiState.value.copy(selectedDuration = minutes)
    }
    
    fun startWorryTimer() {
        // Implementation
    }
    
    fun toggleTimer() {
        // Implementation
    }
    
    fun addWorry(worry: String) {
        // Implementation
    }
    
    fun completeWorryTime() {
        // Implementation
    }
    
    fun addActionPlan(worry: String, plan: ActionPlan) {
        // Implementation
    }
    
    fun resetSession() {
        // Implementation
    }
    
    fun saveSession() {
        // Implementation
    }
}
