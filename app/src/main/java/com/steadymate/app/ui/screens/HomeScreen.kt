package com.steadymate.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.steadymate.app.ui.theme.accessibility.accessibleHeading
import com.steadymate.app.ui.components.Achievement
import com.steadymate.app.ui.components.AchievementRarity
import com.steadymate.app.ui.components.AchievementShowcase
import com.steadymate.app.ui.components.LevelProgress
import com.steadymate.app.ui.components.StreakCounter
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Modern home screen for SteadyMate - focused on user engagement and quick actions
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCheckIn: () -> Unit = {},
    onNavigateToTools: () -> Unit = {},
    onNavigateToCrisis: () -> Unit = {},
    onNavigateToCBT: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Personalized Greeting
        GreetingSection(
            userName = uiState.currentUser?.name ?: "there",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Actions
        QuickActionsSection(
            onNavigateToCheckIn = onNavigateToCheckIn,
            onNavigateToTools = onNavigateToTools,
            onNavigateToCrisis = onNavigateToCrisis,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Streak Counter with celebration effects
        StreakCounter(
            streakCount = uiState.currentUser?.streakCount ?: 5,
            showCelebration = false,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Level Progress
        LevelProgress(
            currentLevel = 12,
            currentXP = 850,
            xpToNextLevel = 1200,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Achievements Showcase
        AchievementShowcase(
            achievements = getSampleAchievements(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Today's Habits
        TodaysHabitsSection(
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // CBT Tools Quick Access
        CBTToolsQuickAccess(
            onNavigateToCBT = onNavigateToCBT,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Wellness Stats
        WellnessStatsSection(
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun GreetingSection(
    userName: String,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time }
    val greeting = when (currentTime.hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Hello"
    }
    
    Column(modifier = modifier) {
        Text(
            text = "$greeting, $userName! 👋",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.accessibleHeading("Greeting: $greeting $userName")
        )
        
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToCheckIn: () -> Unit = {},
    onNavigateToTools: () -> Unit = {},
    onNavigateToCrisis: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .accessibleHeading("Quick actions section")
                .padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.Person,
                title = "Check-in",
                subtitle = "Log your mood",
                onClick = onNavigateToCheckIn,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            )
            
            QuickActionCard(
                icon = Icons.Default.Star,
                title = "Tools",
                subtitle = "Mindfulness",
                onClick = onNavigateToTools,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.weight(1f)
            )
            
            QuickActionCard(
                icon = Icons.Default.Home,
                title = "Support",
                subtitle = "Get help",
                onClick = onNavigateToCrisis,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    // Animated scale for press effect
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "press_scale"
    )
    
    // Shimmer effect for icons
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isPressed) 2.dp else 1.dp,
                color = color.copy(alpha = if (isPressed) 0.8f else 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .scale(scale),
        interactionSource = interactionSource,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isPressed) 2.dp else 8.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.1f),
                            color.copy(alpha = 0.05f),
                            androidx.compose.ui.graphics.Color.Transparent
                        ),
                        radius = 120f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated icon container with gradient
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    color.copy(alpha = 0.8f),
                                    color.copy(alpha = 0.6f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = color.copy(alpha = shimmerAlpha),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ProgressOverviewSection(
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Your Progress",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .accessibleHeading("Progress overview section")
                .padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProgressCard(
                icon = Icons.Default.Add,
                title = "Streak",
                value = "$streakCount",
                subtitle = "days",
                progress = 0.75f,
                modifier = Modifier.weight(1f)
            )
            
            ProgressCard(
                icon = Icons.Default.CheckCircle,
                title = "Habits",
                value = "3/5",
                subtitle = "today",
                progress = 0.6f,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProgressCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "progress_animation"
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun TodaysHabitsSection(
    modifier: Modifier = Modifier
) {
    val mockHabits = remember {
        listOf(
            HabitItem("💧", "Water", true),
            HabitItem("🧘", "Meditate", true),
            HabitItem("📝", "Journal", true),
            HabitItem("🚶", "Walk", false),
            HabitItem("📚", "Read", false)
        )
    }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Habits",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.accessibleHeading("Today's habits section")
            )
            
            Text(
                text = "3/5 completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockHabits) { habit ->
                HabitChip(
                    emoji = habit.emoji,
                    name = habit.name,
                    isCompleted = habit.isCompleted,
                    onClick = { /* Toggle habit */ }
                )
            }
        }
    }
}

@Composable
private fun HabitChip(
    emoji: String,
    name: String,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val infiniteTransition = rememberInfiniteTransition(label = "habit_glow")
    
    // Scale animation for press effect
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "habit_press_scale"
    )
    
    // Pulsing glow effect for completed habits
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isCompleted) 0.3f else 0.0f,
        targetValue = if (isCompleted) 0.8f else 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "habit_glow"
    )
    
    // Rotation animation for emoji when completed
    val rotation by animateFloatAsState(
        targetValue = if (isCompleted) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "emoji_celebration"
    )
    
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .width(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isCompleted) 2.dp else 1.dp,
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = if (isCompleted) {
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                            MaterialTheme.colorScheme.secondary.copy(alpha = glowAlpha * 0.7f)
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    }
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .scale(scale),
        interactionSource = interactionSource,
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.outlinedCardElevation(
            defaultElevation = if (isCompleted) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (isCompleted) {
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                androidx.compose.ui.graphics.Color.Transparent
                            ),
                            radius = 100f
                        )
                    } else {
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                androidx.compose.ui.graphics.Color.Transparent
                            )
                        )
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated emoji with rotation
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (isCompleted) 
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else
                                androidx.compose.ui.graphics.Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.rotate(rotation)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Animated completion indicator with scale effect
                val iconScale by animateFloatAsState(
                    targetValue = if (isCompleted) 1.2f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "completion_icon_scale"
                )
                
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isCompleted) "Completed" else "Not completed",
                    modifier = Modifier
                        .size(18.dp)
                        .scale(iconScale),
                    tint = if (isCompleted) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun WellnessStatsSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Wellness Overview",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .accessibleHeading("Wellness overview section")
                .padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WellnessStatCard(
                icon = Icons.Default.Favorite,
                title = "Mood",
                value = "7.2",
                subtitle = "avg this week",
                modifier = Modifier.weight(1f)
            )
            
            WellnessStatCard(
                icon = Icons.Default.Settings,
                title = "Growth",
                value = "+12%",
                subtitle = "vs last week",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WellnessStatCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CBTToolsQuickAccess(
    onNavigateToCBT: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "CBT Tools",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .accessibleHeading("CBT tools section")
                .padding(bottom = 16.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CBTQuickCard(
                    icon = Icons.Default.Person,
                    title = "Reframe Thoughts",
                    description = "Challenge negative thinking",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigateToCBT("cbt_reframe") }
                )
            }
            
            item {
                CBTQuickCard(
                    icon = Icons.Default.Star, // TODO: Fix AccessTime icon
                    title = "Worry Timer",
                    description = "Manage anxiety",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { onNavigateToCBT("cbt_worry_timer") }
                )
            }
            
            item {
                CBTQuickCard(
                    icon = Icons.Default.Star,
                    title = "Micro Wins",
                    description = "Track gratitude",
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { onNavigateToCBT("cbt_micro_wins") }
                )
            }
        }
    }
}

@Composable
private fun CBTQuickCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.width(160.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = color
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

data class HabitItem(
    val emoji: String,
    val name: String,
    val isCompleted: Boolean
)

/**
 * Sample achievements for demo purposes
 */
@Composable
private fun getSampleAchievements(): List<Achievement> = remember {
    listOf(
        Achievement(
            id = "first_checkin",
            title = "First Steps",
            description = "Complete your first check-in",
            icon = Icons.Default.Favorite,
            isUnlocked = true,
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "week_streak",
            title = "Committed",
            description = "Maintain a 7-day streak",
            icon = Icons.Default.Star,
            isUnlocked = true,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "mindful_master",
            title = "Mindful Master",
            description = "Complete 50 meditation sessions",
            icon = Icons.Default.Person,
            isUnlocked = false,
            progress = 32f,
            maxProgress = 50f,
            rarity = AchievementRarity.EPIC
        ),
        Achievement(
            id = "wellness_warrior",
            title = "Wellness Warrior",
            description = "Achieve 30-day perfect streak",
            icon = Icons.Default.Star,
            isUnlocked = false,
            progress = 5f,
            maxProgress = 30f,
            rarity = AchievementRarity.LEGENDARY
        ),
        Achievement(
            id = "mood_tracker",
            title = "Mood Tracker",
            description = "Log mood for 14 consecutive days",
            icon = Icons.Default.Favorite,
            isUnlocked = false,
            progress = 8f,
            maxProgress = 14f,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "healing_journey",
            title = "Healing Journey",
            description = "Complete all CBT exercises",
            icon = Icons.Default.Settings,
            isUnlocked = false,
            progress = 3f,
            maxProgress = 10f,
            rarity = AchievementRarity.EPIC
        )
    )
}
