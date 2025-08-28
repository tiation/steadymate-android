package com.steadymate.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steadymate.app.data.proto.Theme
import com.steadymate.app.ui.theme.SteadyMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPreferencesScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTheme by remember { mutableStateOf(Theme.ThemeMode.DARK) }
    var useDynamicColors by remember { mutableStateOf(false) }
    var highContrastEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var workoutReminders by remember { mutableStateOf(true) }
    var progressUpdates by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        // Mark this screen as completed when it's shown
        viewModel.markScreenCompleted(2)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Your Preferences") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Customize your experience",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Set up themes and notifications to make SteadyMate feel like home.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Theme Section
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        ThemeOption(
                            icon = Icons.Default.Settings,
                            title = "Follow System",
                            description = "Automatically match your device theme",
                            isSelected = selectedTheme == Theme.ThemeMode.SYSTEM,
                            onSelect = { selectedTheme = Theme.ThemeMode.SYSTEM }
                        )
                        
                        ThemeOption(
                            icon = Icons.Default.Place,
                            title = "Light Theme",
                            description = "Bright and clean interface",
                            isSelected = selectedTheme == Theme.ThemeMode.LIGHT,
                            onSelect = { selectedTheme = Theme.ThemeMode.LIGHT }
                        )
                        
                        ThemeOption(
                            icon = Icons.Default.Star,
                            title = "Dark Theme",
                            description = "Easy on the eyes for low light",
                            isSelected = selectedTheme == Theme.ThemeMode.DARK,
                            onSelect = { selectedTheme = Theme.ThemeMode.DARK }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Use Dynamic Colors",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Based on your wallpaper (Android 12+)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = useDynamicColors,
                                onCheckedChange = { useDynamicColors = it }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "High Contrast",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Improve readability for accessibility",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = highContrastEnabled,
                                onCheckedChange = { highContrastEnabled = it }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Notifications Section
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        NotificationToggle(
                            title = "Enable Notifications",
                            description = "Get helpful reminders and updates",
                            isEnabled = notificationsEnabled,
                            onToggle = { notificationsEnabled = it }
                        )
                        
                        if (notificationsEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            NotificationToggle(
                                title = "Workout Reminders",
                                description = "Daily nudges to keep you on track",
                                isEnabled = workoutReminders,
                                onToggle = { workoutReminders = it }
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            NotificationToggle(
                                title = "Progress Updates",
                                description = "Celebrate your wins and streaks",
                                isEnabled = progressUpdates,
                                onToggle = { progressUpdates = it }
                            )
                        }
                    }
                }
            }
            
            Column {
                Button(
                    onClick = {
                        viewModel.updateThemeSettings(
                            themeMode = selectedTheme,
                            useDynamicColors = useDynamicColors,
                            highContrastEnabled = highContrastEnabled
                        )
                        
                        viewModel.updateNotificationSettings(
                            pushNotifications = notificationsEnabled,
                            emailNotifications = false,
                            workoutReminders = workoutReminders,
                            progressUpdates = progressUpdates,
                            achievementAlerts = progressUpdates
                        )
                        
                        onNext()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = {
                        // Set dark theme as default when using defaults
                        viewModel.updateThemeSettings(
                            themeMode = Theme.ThemeMode.DARK,
                            useDynamicColors = false,
                            highContrastEnabled = false
                        )
                        
                        // Set default notification settings
                        viewModel.updateNotificationSettings(
                            pushNotifications = true,
                            emailNotifications = false,
                            workoutReminders = true,
                            progressUpdates = true,
                            achievementAlerts = true
                        )
                        
                        onNext()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Use defaults")
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NotificationToggle(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreferencesScreenPreview() {
    SteadyMateTheme {
        OnboardingPreferencesScreen(
            viewModel = OnboardingViewModel(
                // Mock repository for preview
                object : com.steadymate.app.data.repository.OnboardingPreferencesRepository {
                    override fun getOnboardingPrefs() = kotlinx.coroutines.flow.flowOf(
                        com.steadymate.app.data.proto.OnboardingPrefs.getDefaultInstance()
                    )
                    override suspend fun updateTheme(theme: com.steadymate.app.data.proto.Theme) {}
                    override suspend fun updateConsentSettings(consentSettings: com.steadymate.app.data.proto.ConsentSettings) {}
                    override suspend fun updateNotificationSettings(notificationSettings: com.steadymate.app.data.proto.NotificationSettings) {}
                    override suspend fun updateOnboardingStatus(onboardingStatus: com.steadymate.app.data.proto.OnboardingStatus) {}
                    override suspend fun markScreenCompleted(screenIndex: Int) {}
                    override suspend fun completeOnboarding() {}
                    override suspend fun resetOnboarding() {}
                    override fun isOnboardingCompleted() = kotlinx.coroutines.flow.flowOf(false)
                    override fun getCurrentOnboardingStep() = kotlinx.coroutines.flow.flowOf(0)
                    override fun getThemeSettings() = kotlinx.coroutines.flow.flowOf(
                        com.steadymate.app.data.proto.Theme.getDefaultInstance()
                    )
                    override fun getConsentSettings() = kotlinx.coroutines.flow.flowOf(
                        com.steadymate.app.data.proto.ConsentSettings.getDefaultInstance()
                    )
                    override fun getNotificationSettings() = kotlinx.coroutines.flow.flowOf(
                        com.steadymate.app.data.proto.NotificationSettings.getDefaultInstance()
                    )
                }
            ),
            onNext = {},
            onBack = {}
        )
    }
}
