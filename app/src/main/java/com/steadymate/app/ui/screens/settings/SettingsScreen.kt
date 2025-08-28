package com.steadymate.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steadymate.app.ui.theme.accessibility.AccessibilityPreferences
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Initialize accessibility manager
    LaunchedEffect(context) {
        viewModel.initializeAccessibilityManager(context)
    }
    
    // Show success message as snackbar
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // TODO: Show snackbar with success message
            // For now, just clear it after a delay
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
    
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
            
            else -> {
                SettingsContent(
                    uiState = uiState,
                    onUpdateNotifications = viewModel::updateNotificationEnabled,
                    onUpdateDarkTheme = viewModel::updateDarkTheme,
                    onUpdateColorPalette = viewModel::updateColorPalette,
                    onUpdateHighContrast = viewModel::updateHighContrast,
                    onUpdateLargeFont = viewModel::updateLargeFont,
                    onUpdateReducedMotion = viewModel::updateReducedMotion,
                    onUpdateDynamicColor = viewModel::updateDynamicColor,
                    onUpdateCrisisHotline = viewModel::updateCrisisHotlineEnabled,
                    onUpdateDataBackup = viewModel::updateDataBackupEnabled,
                    onUpdateAnalytics = viewModel::updateAnalyticsEnabled,
                    onClearData = { viewModel.clearAppData(context) },
                    onExportData = { viewModel.exportUserData(context) },
                    onResetOnboarding = viewModel::resetOnboarding
                )
            }
        }
        
        // Success message overlay
        uiState.successMessage?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onUpdateNotifications: (Boolean) -> Unit,
    onUpdateDarkTheme: (Boolean) -> Unit,
    onUpdateColorPalette: (com.steadymate.app.data.proto.Theme.ColorPalette) -> Unit,
    onUpdateHighContrast: (Boolean) -> Unit,
    onUpdateLargeFont: (Boolean) -> Unit,
    onUpdateReducedMotion: (Boolean) -> Unit,
    onUpdateDynamicColor: (Boolean) -> Unit,
    onUpdateCrisisHotline: (Boolean) -> Unit,
    onUpdateDataBackup: (Boolean) -> Unit,
    onUpdateAnalytics: (Boolean) -> Unit,
    onClearData: () -> Unit,
    onExportData: () -> Unit,
    onResetOnboarding: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Customize your SteadyMate experience",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        // Appearance Section
        item {
            SettingsSection(
                title = "Appearance",
                icon = Icons.Default.Settings
            ) {
                SettingsSwitchItem(
                    title = "Dark Theme",
                    subtitle = "Use dark mode interface",
                    checked = uiState.userPreferences.darkThemeEnabled,
                    onCheckedChange = onUpdateDarkTheme,
                    icon = Icons.Default.Settings
                )
                
                SettingsSwitchItem(
                    title = "Dynamic Colors",
                    subtitle = "Adapt to your device's color scheme",
                    checked = uiState.accessibilityPreferences.isDynamicColor,
                    onCheckedChange = onUpdateDynamicColor,
                    icon = Icons.Default.Settings
                )
                
                SettingsSwitchItem(
                    title = "High Contrast",
                    subtitle = "Improve visibility with higher contrast",
                    checked = uiState.accessibilityPreferences.isHighContrast,
                    onCheckedChange = onUpdateHighContrast,
                    icon = Icons.Default.Settings
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Color Palette",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ColorPaletteSelector(
                    selectedPalette = uiState.currentColorPalette,
                    onPaletteSelected = onUpdateColorPalette
                )
            }
        }
        
        // Accessibility Section
        item {
            SettingsSection(
                title = "Accessibility",
                icon = Icons.Default.Settings
            ) {
                SettingsSwitchItem(
                    title = "Large Font",
                    subtitle = "Increase text size for better readability",
                    checked = uiState.accessibilityPreferences.isLargeFont,
                    onCheckedChange = onUpdateLargeFont,
                    icon = Icons.Default.Settings
                )
                
                SettingsSwitchItem(
                    title = "Reduced Motion",
                    subtitle = "Minimize animations and transitions",
                    checked = uiState.accessibilityPreferences.isReducedMotion,
                    onCheckedChange = onUpdateReducedMotion,
                    icon = Icons.Default.Settings
                )
            }
        }
        
        // Notifications Section
        item {
            SettingsSection(
                title = "Notifications",
                icon = Icons.Default.Notifications
            ) {
                SettingsSwitchItem(
                    title = "Push Notifications",
                    subtitle = "Receive reminders and updates",
                    checked = uiState.userPreferences.notificationsEnabled,
                    onCheckedChange = onUpdateNotifications,
                    icon = Icons.Default.Notifications
                )
            }
        }
        
        // Safety & Privacy Section
        item {
            SettingsSection(
                title = "Safety & Privacy",
                icon = Icons.Default.Settings
            ) {
                SettingsSwitchItem(
                    title = "Crisis Hotline Access",
                    subtitle = "Quick access to crisis support",
                    checked = uiState.userPreferences.crisisHotlineEnabled,
                    onCheckedChange = onUpdateCrisisHotline,
                    icon = Icons.Default.Phone
                )
                
                SettingsSwitchItem(
                    title = "Data Backup",
                    subtitle = "Backup your data to cloud storage",
                    checked = uiState.userPreferences.dataBackupEnabled,
                    onCheckedChange = onUpdateDataBackup,
                    icon = Icons.Default.Settings
                )
                
                SettingsSwitchItem(
                    title = "Analytics",
                    subtitle = "Help improve the app with usage data",
                    checked = uiState.userPreferences.analyticsEnabled,
                    onCheckedChange = onUpdateAnalytics,
                    icon = Icons.Default.Settings
                )
            }
        }
        
        // Data Management Section
        item {
            SettingsSection(
                title = "Data Management",
                icon = Icons.Default.Settings
            ) {
                SettingsActionItem(
                    title = "Export Data",
                    subtitle = "Download your data",
                    onClick = onExportData,
                    icon = Icons.Default.Settings
                )
                
                SettingsActionItem(
                    title = "Reset Onboarding",
                    subtitle = "Go through the setup process again",
                    onClick = onResetOnboarding,
                    icon = Icons.Default.Refresh,
                    textColor = MaterialTheme.colorScheme.primary
                )
                
                SettingsActionItem(
                    title = "Clear All Data",
                    subtitle = "Remove all your data from the app",
                    onClick = onClearData,
                    icon = Icons.Default.Delete,
                    textColor = MaterialTheme.colorScheme.error
                )
            }
        }
        
        // App Information
        item {
            SettingsSection(
                title = "About",
                icon = Icons.Default.Info
            ) {
                SettingsInfoItem(
                    title = "Version",
                    value = uiState.appVersion
                )
                
                SettingsInfoItem(
                    title = "Build",
                    value = uiState.buildNumber
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            content()
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsActionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ColorPaletteSelector(
    selectedPalette: com.steadymate.app.data.proto.Theme.ColorPalette,
    onPaletteSelected: (com.steadymate.app.data.proto.Theme.ColorPalette) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        PaletteOption(
            title = "Beautiful",
            description = "Soft purples and pinks (default)",
            isSelected = selectedPalette == com.steadymate.app.data.proto.Theme.ColorPalette.BEAUTIFUL,
            onSelect = { onPaletteSelected(com.steadymate.app.data.proto.Theme.ColorPalette.BEAUTIFUL) },
            previewColor = Color(0xFF9C27B0) // Purple
        )
        
        PaletteOption(
            title = "Masculine",
            description = "Bold electric blues",
            isSelected = selectedPalette == com.steadymate.app.data.proto.Theme.ColorPalette.MASCULINE,
            onSelect = { onPaletteSelected(com.steadymate.app.data.proto.Theme.ColorPalette.MASCULINE) },
            previewColor = Color(0xFF2196F3) // Blue
        )
        
        PaletteOption(
            title = "Classic",
            description = "Traditional Material 3 purple",
            isSelected = selectedPalette == com.steadymate.app.data.proto.Theme.ColorPalette.CLASSIC,
            onSelect = { onPaletteSelected(com.steadymate.app.data.proto.Theme.ColorPalette.CLASSIC) },
            previewColor = Color(0xFF673AB7) // Deep Purple
        )
        
        PaletteOption(
            title = "High Contrast",
            description = "Ultra high contrast for accessibility",
            isSelected = selectedPalette == com.steadymate.app.data.proto.Theme.ColorPalette.HIGH_CONTRAST,
            onSelect = { onPaletteSelected(com.steadymate.app.data.proto.Theme.ColorPalette.HIGH_CONTRAST) },
            previewColor = Color(0xFF000000) // Black
        )
    }
}

@Composable
private fun PaletteOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    previewColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Color preview circle
        Card(
            modifier = Modifier.size(24.dp),
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(
                containerColor = previewColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {}
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
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
private fun SettingsInfoItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
