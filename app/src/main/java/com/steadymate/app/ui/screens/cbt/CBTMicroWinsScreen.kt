package com.steadymate.app.ui.screens.cbt

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.collectAsState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * CBT Micro Wins Screen - Daily gratitude and achievement tracking (Three Good Things exercise)
 * Helps users focus on positive experiences and build resilience through gratitude practice
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CBTMicroWinsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CBTMicroWinsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Micro Wins") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (uiState.todayEntry != null) {
                    IconButton(onClick = viewModel::exportEntry) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
                
                IconButton(onClick = viewModel::showHistory) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "View History"
                    )
                }
            }
        )
        
        // Content
        when (uiState.currentView) {
            MicroWinsView.DAILY_ENTRY -> {
                DailyEntryScreen(
                    todayEntry = uiState.todayEntry,
                    isLoading = uiState.isLoading,
                    streak = uiState.currentStreak,
                    onSaveEntry = viewModel::saveDailyEntry,
                    onEditEntry = viewModel::editTodayEntry
                )
            }
            MicroWinsView.HISTORY -> {
                HistoryScreen(
                    entries = uiState.pastEntries,
                    onBackToToday = viewModel::showDailyEntry,
                    onDeleteEntry = viewModel::deleteEntry
                )
            }
        }
    }
}

@Composable
private fun DailyEntryScreen(
    todayEntry: MicroWinsEntry?,
    isLoading: Boolean,
    streak: Int,
    onSaveEntry: (MicroWinsEntry) -> Unit,
    onEditEntry: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StreakCard(streak = streak)
        }
        
        if (todayEntry == null) {
            item {
                IntroductionCard()
            }
            
            item {
                NewEntryForm(
                    onSaveEntry = onSaveEntry,
                    isLoading = isLoading
                )
            }
        } else {
            item {
                CompletedEntryCard(
                    entry = todayEntry,
                    onEdit = onEditEntry
                )
            }
        }
        
        item {
            TipsCard()
        }
    }
}

@Composable
private fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "$streak day streak",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = if (streak == 0) "Start your journey today!" 
                           else if (streak == 1) "Great start! Keep it going"
                           else "Amazing consistency!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun IntroductionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Three Good Things",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = "Research shows that writing down three positive things that happened each day can significantly improve your mood and overall well-being.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun NewEntryForm(
    onSaveEntry: (MicroWinsEntry) -> Unit,
    isLoading: Boolean
) {
    var win1 by remember { mutableStateOf("") }
    var win1Reflection by remember { mutableStateOf("") }
    var win2 by remember { mutableStateOf("") }
    var win2Reflection by remember { mutableStateOf("") }
    var win3 by remember { mutableStateOf("") }
    var win3Reflection by remember { mutableStateOf("") }
    
    val wins = listOf(
        Pair(win1, win1Reflection),
        Pair(win2, win2Reflection),
        Pair(win3, win3Reflection)
    )
    
    val isValid = wins.all { it.first.isNotBlank() }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Today's Three Good Things",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        wins.forEachIndexed { index, (win, reflection) ->
            WinEntryCard(
                index = index + 1,
                winText = win,
                reflectionText = reflection,
                onWinChange = { newWin ->
                    when (index) {
                        0 -> win1 = newWin
                        1 -> win2 = newWin
                        2 -> win3 = newWin
                    }
                },
                onReflectionChange = { newReflection ->
                    when (index) {
                        0 -> win1Reflection = newReflection
                        1 -> win2Reflection = newReflection
                        2 -> win3Reflection = newReflection
                    }
                }
            )
        }
        
        Button(
            onClick = {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val entry = MicroWinsEntry(
                    date = today,
                    wins = listOf(
                        MicroWin(win1, win1Reflection),
                        MicroWin(win2, win2Reflection),
                        MicroWin(win3, win3Reflection)
                    )
                )
                onSaveEntry(entry)
            },
            enabled = isValid && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Save Today's Wins",
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
private fun WinEntryCard(
    index: Int,
    winText: String,
    reflectionText: String,
    onWinChange: (String) -> Unit,
    onReflectionChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = index.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Good thing #$index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = winText,
                onValueChange = onWinChange,
                label = { Text("What good thing happened?") },
                placeholder = { Text("I accomplished...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = reflectionText,
                onValueChange = onReflectionChange,
                label = { Text("Why was this meaningful? (Optional)") },
                placeholder = { Text("This made me feel...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
    }
}

@Composable
private fun CompletedEntryCard(
    entry: MicroWinsEntry,
    onEdit: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Today's Wins Recorded!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "Great job focusing on the positive!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            entry.wins.forEachIndexed { index, win ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                CompletedWinItem(
                    index = index + 1,
                    win = win
                )
            }
        }
    }
}

@Composable
private fun CompletedWinItem(
    index: Int,
    win: MicroWin
) {
    Column {
        Row(
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
            
            Column {
                Text(
                    text = win.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                if (win.reflection.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = win.reflection,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TipsCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Tips for Better Micro Wins:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val tips = listOf(
                "Include both big and small accomplishments",
                "Notice moments of connection with others",
                "Acknowledge progress, not just perfection",
                "Be specific - details make wins more meaningful"
            )
            
            tips.forEach { tip ->
                Text(
                    text = "• $tip",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun HistoryScreen(
    entries: List<MicroWinsEntry>,
    onBackToToday: () -> Unit,
    onDeleteEntry: (MicroWinsEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your Wins History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                TextButton(onClick = onBackToToday) {
                    Text("Back to Today")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        if (entries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "No entries yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Text(
                            text = "Start by recording today's wins!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            items(entries) { entry ->
                HistoryEntryCard(
                    entry = entry,
                    onDelete = { onDeleteEntry(entry) }
                )
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(
    entry: MicroWinsEntry,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = entry.date.toString(), // You'd format this properly
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            entry.wins.forEachIndexed { index, win ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                HistoryWinItem(
                    index = index + 1,
                    win = win
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this entry? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryWinItem(
    index: Int,
    win: MicroWin
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(20.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = win.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (win.reflection.isNotBlank()) {
                Text(
                    text = win.reflection,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// Data classes and enums
enum class MicroWinsView {
    DAILY_ENTRY,
    HISTORY
}

data class MicroWin(
    val description: String,
    val reflection: String = ""
)

data class MicroWinsEntry(
    val date: LocalDate,
    val wins: List<MicroWin>
)

data class MicroWinsUiState(
    val currentView: MicroWinsView = MicroWinsView.DAILY_ENTRY,
    val todayEntry: MicroWinsEntry? = null,
    val pastEntries: List<MicroWinsEntry> = emptyList(),
    val currentStreak: Int = 0,
    val isLoading: Boolean = false
)

// Mock ViewModel (you'd implement the actual ViewModel)
class CBTMicroWinsViewModel : androidx.lifecycle.ViewModel() {
    private val _uiState = mutableStateOf(MicroWinsUiState())
    val uiState: State<MicroWinsUiState> = _uiState
    
    fun saveDailyEntry(entry: MicroWinsEntry) {
        // Implementation: save to database, update streak
    }
    
    fun editTodayEntry() {
        // Implementation: allow editing today's entry
    }
    
    fun showHistory() {
        _uiState.value = _uiState.value.copy(currentView = MicroWinsView.HISTORY)
    }
    
    fun showDailyEntry() {
        _uiState.value = _uiState.value.copy(currentView = MicroWinsView.DAILY_ENTRY)
    }
    
    fun deleteEntry(entry: MicroWinsEntry) {
        // Implementation: delete entry and recalculate streak
    }
    
    fun exportEntry() {
        // Implementation: export/share functionality
    }
}
