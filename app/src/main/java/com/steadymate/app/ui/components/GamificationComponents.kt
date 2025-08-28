package com.steadymate.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Achievement badge data class
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean,
    val progress: Float = 0f,
    val maxProgress: Float = 1f,
    val rarity: AchievementRarity = AchievementRarity.COMMON
)

enum class AchievementRarity {
    COMMON, RARE, EPIC, LEGENDARY
}

/**
 * Animated streak counter with flame effects
 */
@Composable
fun StreakCounter(
    streakCount: Int,
    modifier: Modifier = Modifier,
    showCelebration: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame_glow")
    
    // Flame glow animation
    val flameGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "flame_glow"
    )
    
    // Celebration bounce animation
    val celebrationScale by animateFloatAsState(
        targetValue = if (showCelebration) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "celebration_scale"
    )
    
    ElevatedCard(
        modifier = modifier
            .scale(celebrationScale),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF6B35).copy(alpha = 0.1f),
                            Color(0xFFFF073A).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = 150f
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated flame icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFF6B35).copy(alpha = flameGlow),
                                    Color(0xFFFF073A).copy(alpha = flameGlow * 0.8f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFBF00).copy(alpha = flameGlow),
                                    Color(0xFFFF6B35).copy(alpha = flameGlow * 0.7f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Streak flame",
                        modifier = Modifier
                            .size(32.dp)
                            .scale(if (showCelebration) 1.1f else 1.0f),
                        tint = Color(0xFFFFBF00)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Streak count
                Text(
                    text = streakCount.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = if (streakCount == 1) "day streak" else "day streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                // Motivational message based on streak
                val message = when {
                    streakCount >= 30 -> "🏆 Legendary!"
                    streakCount >= 21 -> "⚡ On Fire!"
                    streakCount >= 14 -> "💪 Crushing It!"
                    streakCount >= 7 -> "🚀 Great Work!"
                    streakCount >= 3 -> "✨ Keep Going!"
                    else -> "🌟 You Got This!"
                }
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Achievement badge component with unlock animations
 */
@Composable
fun AchievementBadge(
    achievement: Achievement,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "badge_glow")
    var isNewlyUnlocked by remember { mutableStateOf(false) }
    
    // Glow effect for unlocked achievements
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (achievement.isUnlocked) 0.4f else 0.0f,
        targetValue = if (achievement.isUnlocked) 0.8f else 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "badge_glow"
    )
    
    // Unlock celebration animation
    val celebrationScale by animateFloatAsState(
        targetValue = if (isNewlyUnlocked) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "unlock_celebration"
    )
    
    LaunchedEffect(achievement.isUnlocked) {
        if (achievement.isUnlocked) {
            isNewlyUnlocked = true
            delay(1000)
            isNewlyUnlocked = false
        }
    }
    
    val rarityColors = when (achievement.rarity) {
        AchievementRarity.COMMON -> listOf(Color(0xFF9E9E9E), Color(0xFF757575))
        AchievementRarity.RARE -> listOf(Color(0xFF2196F3), Color(0xFF1976D2))
        AchievementRarity.EPIC -> listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2))
        AchievementRarity.LEGENDARY -> listOf(Color(0xFFFF9800), Color(0xFFF57C00))
    }
    
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .size(120.dp)
            .scale(celebrationScale)
            .border(
                width = if (achievement.isUnlocked) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = if (achievement.isUnlocked) {
                        rarityColors.map { it.copy(alpha = glowAlpha) }
                    } else {
                        listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (achievement.isUnlocked) 8.dp else 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (achievement.isUnlocked) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (achievement.isUnlocked) {
                        Brush.radialGradient(
                            colors = rarityColors.map { it.copy(alpha = 0.1f) } + Color.Transparent,
                            radius = 80f
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Achievement icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = if (achievement.isUnlocked) {
                                Brush.linearGradient(rarityColors)
                            } else {
                                Brush.linearGradient(
                                    listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))
                                )
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = achievement.icon,
                        contentDescription = achievement.title,
                        modifier = Modifier.size(28.dp),
                        tint = if (achievement.isUnlocked) {
                            Color.White
                        } else {
                            Color.Gray
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Achievement title
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (achievement.isUnlocked) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (achievement.isUnlocked) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                
                // Progress bar for partially completed achievements
                if (!achievement.isUnlocked && achievement.progress > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LinearProgressIndicator(
                        progress = { achievement.progress / achievement.maxProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp)),
                        color = rarityColors.first(),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

/**
 * Achievement showcase with horizontal scrolling
 */
@Composable
fun AchievementShowcase(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🏆 Achievements",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            val unlockedCount = achievements.count { it.isUnlocked }
            Text(
                text = "$unlockedCount/${achievements.size}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(achievements) { achievement ->
                AchievementBadge(
                    achievement = achievement,
                    onClick = { /* Show achievement details */ }
                )
            }
        }
    }
}

/**
 * Level progress component with XP bar
 */
@Composable
fun LevelProgress(
    currentLevel: Int,
    currentXP: Int,
    xpToNextLevel: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentXP.toFloat() / xpToNextLevel.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "xp_progress"
    )
    
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Level $currentLevel",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "$currentXP / $xpToNextLevel XP",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // XP Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val nextLevelText = if (currentLevel >= 50) {
                    "🎯 Max Level Reached!"
                } else {
                    "⚡ ${xpToNextLevel - currentXP} XP to Level ${currentLevel + 1}"
                }
                
                Text(
                    text = nextLevelText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/**
 * Celebration popup for achievements
 */
@Composable
fun CelebrationPopup(
    isVisible: Boolean,
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Star,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rotationAnimatable = remember { Animatable(0f) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            rotationAnimatable.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) +
                scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) +
                slideInVertically(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(200)) +
                scaleOut(animationSpec = tween(200)) +
                slideOutVertically(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700).copy(alpha = 0.2f),
                                Color(0xFFFF6B35).copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 200f
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Celebration",
                        modifier = Modifier
                            .size(64.dp)
                            .rotate(rotationAnimatable.value),
                        tint = Color(0xFFFFD700)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
