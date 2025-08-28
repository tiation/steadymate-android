package com.steadymate.app.ui.screens.tools

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steadymate.app.domain.model.*
import com.steadymate.app.ui.utils.HapticPattern
import com.steadymate.app.ui.utils.UIInteraction
import com.steadymate.app.ui.utils.rememberHapticsHelper

/**
 * Tools screen with breathing exercises and mental health tools
 */
@Composable
fun ToolsScreen(
    modifier: Modifier = Modifier,
    viewModel: ToolsViewModel = hiltViewModel(),
    onNavigateToCBT: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val breathingSession by viewModel.breathingSession.collectAsStateWithLifecycle()
    val hapticsHelper = rememberHapticsHelper()
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { viewModel.clearError() }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            breathingSession != null -> {
                // Show breathing session screen
                BreathingSessionScreen(
                    sessionState = breathingSession!!,
                    onPause = { 
                        hapticsHelper.performHaptic(HapticPattern.GENTLE_PULSE)
                        viewModel.pauseBreathingExercise()
                    },
                    onResume = {
                        hapticsHelper.performHaptic(HapticPattern.SUCCESS)
                        viewModel.resumeBreathingExercise() 
                    },
                    onStop = { 
                        hapticsHelper.performHaptic(HapticPattern.GENTLE_PULSE)
                        viewModel.stopBreathingExercise() 
                    },
                    onPhaseChange = { phase ->
                        hapticsHelper.performBreathingHaptic(phase)
                    }
                )
            }
            
            else -> {
                // Show main tools content
                ToolsContent(
                    uiState = uiState,
                    onCategoryFilter = viewModel::filterByCategory,
                    onExerciseStart = { exercise ->
                        hapticsHelper.performHaptic(HapticPattern.SUCCESS)
                        viewModel.startBreathingExercise(exercise)
                    },
                    onNavigateToCBT = onNavigateToCBT
                )
            }
        }
    }
}

@Composable
private fun ToolsContent(
    uiState: ToolsUiState,
    onCategoryFilter: (BreathingCategory?) -> Unit,
    onExerciseStart: (BreathingExercise) -> Unit,
    onNavigateToCBT: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mental Health Tools",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Breathing exercises and mindfulness tools",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        // CBT Tools Section
        item {
            CBTToolsSection(onNavigateToCBT = onNavigateToCBT)
        }
        
        // Quick Access Tools
        item {
            QuickAccessTools(onExerciseStart = onExerciseStart)
        }
        
        // Category Filters
        item {
            CategoryFilters(
                selectedCategory = uiState.selectedCategory,
                onCategorySelect = onCategoryFilter
            )
        }
        
        // Breathing Exercises
        item {
            Text(
                text = "Breathing Exercises",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }
        
        items(getFilteredExercises(uiState)) { exercise ->
            BreathingExerciseCard(
                exercise = exercise,
                onStart = { onExerciseStart(exercise) }
            )
        }
    }
}

@Composable
private fun QuickAccessTools(
    onExerciseStart: (BreathingExercise) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Reset",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickToolButton(
                    icon = Icons.Default.Favorite,
                    text = "Box Breath",
                    onClick = { 
                        BreathingExercise.getById("box_breathing")?.let { exercise ->
                            onExerciseStart(exercise)
                        }
                    }
                )
                
                QuickToolButton(
                    icon = Icons.Default.Search,
                    text = "5-4-3-2-1",
                    onClick = { 
                        // Quick grounding exercise - could navigate to a dedicated screen
                        // For now, start the 4-7-8 breathing as anxiety relief
                        BreathingExercise.getById("four_seven_eight")?.let { exercise ->
                            onExerciseStart(exercise)
                        }
                    }
                )
                
                QuickToolButton(
                    icon = Icons.Default.Favorite,
                    text = "Calm",
                    onClick = { 
                        // Start coherent breathing for calm
                        BreathingExercise.getById("coherent_breathing")?.let { exercise ->
                            onExerciseStart(exercise)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CategoryFilters(
    selectedCategory: BreathingCategory?,
    onCategorySelect: (BreathingCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            FilterChip(
                onClick = { onCategorySelect(null) },
                label = { Text("All") },
                selected = selectedCategory == null
            )
        }
        
        items(BreathingCategory.values()) { category ->
            FilterChip(
                onClick = { onCategorySelect(category) },
                label = { Text(category.displayName) },
                selected = selectedCategory == category
            )
        }
    }
}

@Composable
private fun BreathingExerciseCard(
    exercise: BreathingExercise,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStart() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = exercise.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = exercise.difficulty.displayName,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.height(28.dp)
                    )
                    
                    Text(
                        text = exercise.formattedDuration,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Benefits
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(exercise.benefits.take(3)) { benefit ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = benefit,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreathingSessionScreen(
    sessionState: BreathingSessionState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onPhaseChange: (BreathingPhase) -> Unit
) {
    // Track phase changes for haptic feedback
    var lastPhase by remember { mutableStateOf(sessionState.currentPhase) }
    LaunchedEffect(sessionState.currentPhase) {
        if (sessionState.currentPhase != lastPhase) {
            onPhaseChange(sessionState.currentPhase)
            lastPhase = sessionState.currentPhase
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Exercise Name
        Text(
            text = sessionState.exercise.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress info
        Text(
            text = "Cycle ${sessionState.currentCycle + 1} of ${sessionState.exercise.totalCycles}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Animated breathing circle
        BreathingCircle(
            scale = sessionState.animationScale,
            phase = sessionState.currentPhase
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Current phase instruction
        Text(
            text = sessionState.currentPhase.instruction,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Time remaining
        Text(
            text = sessionState.formattedRemainingTime,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Light
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pause/Resume button
            Button(
                onClick = if (sessionState.isPaused) onResume else onPause,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (sessionState.isPaused) 
                        MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = if (sessionState.isPaused) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                    contentDescription = if (sessionState.isPaused) "Resume" else "Pause",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (sessionState.isPaused) "Resume" else "Pause")
            }
            
            // Stop button
            OutlinedButton(
                onClick = onStop
            ) {
                Icon(
                                imageVector = Icons.Default.Close,
                    contentDescription = "Stop",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop")
            }
        }
    }
}

@Composable
private fun BreathingCircle(
    scale: Float,
    phase: BreathingPhase
) {
    val animatedScale = remember { Animatable(scale) }
    
    LaunchedEffect(scale) {
        animatedScale.animateTo(
            targetValue = scale,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }
    
    val circleColor = when (phase) {
        BreathingPhase.INHALE -> MaterialTheme.colorScheme.primary
        BreathingPhase.HOLD_INHALE -> MaterialTheme.colorScheme.secondary
        BreathingPhase.EXHALE -> MaterialTheme.colorScheme.tertiary
        BreathingPhase.HOLD_EXHALE -> MaterialTheme.colorScheme.error
        BreathingPhase.COMPLETE -> MaterialTheme.colorScheme.surface
    }
    
    Canvas(
        modifier = Modifier
            .size(200.dp)
            .scale(animatedScale.value)
    ) {
        drawBreathingCircle(circleColor)
    }
}

private fun DrawScope.drawBreathingCircle(color: Color) {
    val radius = size.minDimension / 2
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.6f),
                color.copy(alpha = 0.2f)
            ),
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

@Composable
private fun CBTToolsSection(
    onNavigateToCBT: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "CBT Tools",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Text(
                text = "Evidence-based cognitive behavioral therapy techniques",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CBTToolCard(
                    title = "Thought\nReframing",
                    description = "Challenge negative thoughts",
                    icon = Icons.Default.Person,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigateToCBT("reframe") },
                    modifier = Modifier.weight(1f)
                )
                
                CBTToolCard(
                    title = "Worry\nTimer",
                    description = "Manage anxiety & parking worries",
                    icon = Icons.Default.Settings,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { onNavigateToCBT("worry_timer") },
                    modifier = Modifier.weight(1f)
                )
                
                CBTToolCard(
                    title = "Micro\nWins",
                    description = "Track gratitude & achievements",
                    icon = Icons.Default.Star,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { onNavigateToCBT("micro_wins") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CBTToolCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .aspectRatio(0.8f),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2
            )
        }
    }
}

private fun getFilteredExercises(uiState: ToolsUiState): List<BreathingExercise> {
    return if (uiState.selectedCategory != null) {
        uiState.exercisesByCategory[uiState.selectedCategory] ?: emptyList()
    } else {
        uiState.availableExercises
    }
}
