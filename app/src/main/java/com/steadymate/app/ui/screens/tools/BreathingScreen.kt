package com.steadymate.app.ui.screens.tools

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steadymate.app.domain.model.BreathingPhase
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    onNavigateBack: () -> Unit,
    viewModel: BreathingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    // Trigger haptic feedback on phase changes
    LaunchedEffect(uiState.currentPhase) {
        if (uiState.isActive && uiState.currentPhase != BreathingPhase.COMPLETE) {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
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
                Text(
                    text = if (uiState.isActive) "Breathe with the circle" else "Ready to breathe?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
                
                if (!uiState.isActive) {
                    Text(
                        text = "Take a moment to center yourself",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Breathing Circle Animation
        BreathingCircle(
            phase = uiState.currentPhase,
            progress = uiState.phaseProgress,
            isActive = uiState.isActive,
            modifier = Modifier.size(280.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phase and instruction text
        if (uiState.isActive) {
            Text(
                text = when (uiState.currentPhase) {
                    BreathingPhase.INHALE -> "Breathe in..."
                    BreathingPhase.HOLD_INHALE -> "Hold..."
                    BreathingPhase.EXHALE -> "Breathe out..."
                    BreathingPhase.HOLD_EXHALE -> "Hold..."
                    BreathingPhase.COMPLETE -> "Complete"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${uiState.completedRounds + 1} / ${uiState.targetRounds}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Exercise Type Selection (when not active)
        if (!uiState.isActive && !uiState.isCompleted) {
            ExerciseTypeSelector(
                selectedType = uiState.exerciseType,
                onTypeSelected = viewModel::selectExerciseType,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Target rounds selector
            RoundsSelector(
                targetRounds = uiState.targetRounds,
                onRoundsChanged = viewModel::setTargetRounds,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            // Audio Controls (when not active)
            AudioControlsSection(
                isSoundEnabled = uiState.isSoundEnabled,
                soundVolume = uiState.soundVolume,
                onToggleSound = viewModel::toggleSoundEnabled,
                onVolumeChanged = viewModel::setSoundVolume,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.isActive) {
                // Pause button
                FilledTonalButton(
                    onClick = viewModel::pauseExercise,
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Pause"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pause")
                }

                // Stop button
                OutlinedButton(
                    onClick = viewModel::stopExercise,
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Stop"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop")
                }
            } else if (uiState.isCompleted) {
                // Show completion state
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎉 Great job!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Breathing session completed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                // Start button
                Button(
                    onClick = viewModel::startExercise,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Breathing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Before/After mood rating
        if (!uiState.isActive) {
            MoodRatingSection(
                beforeMood = uiState.moodBefore,
                afterMood = uiState.moodAfter,
                onBeforeMoodChanged = viewModel::setMoodBefore,
                onAfterMoodChanged = viewModel::setMoodAfter,
                isCompleted = uiState.isCompleted,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Save session button (only after completion)
        if (uiState.isCompleted && uiState.moodAfter != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    viewModel.saveSession()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Session & Continue")
            }
        }
    }
}

@Composable
private fun BreathingCircle(
    phase: BreathingPhase,
    progress: Float,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    // Animate circle scale based on breathing phase
    val targetScale = when (phase) {
        BreathingPhase.INHALE -> 1f + (progress * 0.3f) // Grow during inhale
        BreathingPhase.HOLD_INHALE -> 1.3f // Stay large during hold
        BreathingPhase.EXHALE -> 1.3f - (progress * 0.3f) // Shrink during exhale
        BreathingPhase.HOLD_EXHALE -> 1f // Stay small during hold
        BreathingPhase.COMPLETE -> 1f
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isActive) targetScale else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "circle_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .scale(animatedScale)
        ) {
            drawBreathingCircle(
                phase = phase,
                progress = progress,
                isActive = isActive,
                primaryColor = primaryColor,
                surfaceColor = surfaceColor
            )
        }
        
        // Center guidance dots
        if (isActive) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val dotAlpha by animateFloatAsState(
                        targetValue = if (index < 3) 1f else 0.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_alpha_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(animatedScale * 0.7f),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = primaryColor.copy(alpha = dotAlpha),
                                radius = 4.dp.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBreathingCircle(
    phase: BreathingPhase,
    progress: Float,
    isActive: Boolean,
    primaryColor: Color,
    surfaceColor: Color
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val baseRadius = size.minDimension / 3
    
    // Background circle
    drawCircle(
        color = primaryColor.copy(alpha = 0.1f),
        radius = baseRadius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY),
        style = Stroke(width = 4.dp.toPx())
    )
    
    if (isActive) {
        // Progress arc
        val sweepAngle = progress * 360f
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(
                centerX - baseRadius,
                centerY - baseRadius
            ),
            size = androidx.compose.ui.geometry.Size(baseRadius * 2, baseRadius * 2),
            style = Stroke(
                width = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
        
        // Breathing guide - particles around the circle
        repeat(12) { index ->
            val angle = (index * 30f) * (Math.PI / 180f)
            val particleRadius = baseRadius + (20 * sin(progress * Math.PI)).toFloat()
            val x = centerX + cos(angle) * particleRadius
            val y = centerY + sin(angle) * particleRadius
            
            drawCircle(
                color = primaryColor.copy(alpha = 0.6f),
                radius = 3.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())
            )
        }
    }
}

@Composable
private fun ExerciseTypeSelector(
    selectedType: BreathingExerciseType,
    onTypeSelected: (BreathingExerciseType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Breathing Exercise",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BreathingExerciseType.values().forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { onTypeSelected(type) },
                        label = {
                            Column {
                                Text(
                                    text = when (type) {
                                        BreathingExerciseType.BOX -> "Box Breathing"
                                        BreathingExerciseType.FOUR_SEVEN_EIGHT -> "4-7-8 Technique"
                                        BreathingExerciseType.SIMPLE -> "Simple Breathing"
                                    },
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = when (type) {
                                        BreathingExerciseType.BOX -> "4 seconds each phase"
                                        BreathingExerciseType.FOUR_SEVEN_EIGHT -> "Inhale 4, Hold 7, Exhale 8"
                                        BreathingExerciseType.SIMPLE -> "Natural rhythm"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundsSelector(
    targetRounds: Int,
    onRoundsChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Number of Rounds",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { if (targetRounds > 1) onRoundsChanged(targetRounds - 1) }
                ) {
                    Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                
                Text(
                    text = targetRounds.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { if (targetRounds < 20) onRoundsChanged(targetRounds + 1) }
                ) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MoodRatingSection(
    beforeMood: Int?,
    afterMood: Int?,
    onBeforeMoodChanged: (Int) -> Unit,
    onAfterMoodChanged: (Int) -> Unit,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "How are you feeling?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Before mood
            Text(
                text = "Before breathing:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Slider(
                value = (beforeMood ?: 5).toFloat(),
                onValueChange = { onBeforeMoodChanged(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                modifier = Modifier.fillMaxWidth()
            )
            
            // After mood (only show if completed)
            if (isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "After breathing:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Slider(
                    value = (afterMood ?: 5).toFloat(),
                    onValueChange = { onAfterMoodChanged(it.toInt()) },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AudioControlsSection(
    isSoundEnabled: Boolean,
    soundVolume: Float,
    onToggleSound: () -> Unit,
    onVolumeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Breathing Sound",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Sound toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSoundEnabled) "🔊" else "🔇",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Enable breathing sounds",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Switch(
                    checked = isSoundEnabled,
                    onCheckedChange = { onToggleSound() }
                )
            }
            
            // Volume control (only show when sound is enabled)
            if (isSoundEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Volume: ${(soundVolume * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = soundVolume,
                    onValueChange = onVolumeChanged,
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Sound will automatically match your breathing rhythm",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

enum class BreathingExerciseType {
    BOX,
    FOUR_SEVEN_EIGHT,
    SIMPLE
}
