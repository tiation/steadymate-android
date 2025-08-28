package com.steadymate.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.steadymate.app.ui.theme.SteadyMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPermissionsScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(false) }
    var analyticsConsent by remember { mutableStateOf(false) }
    var crashReportingConsent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        // Mark this screen as completed when it's shown
        viewModel.markScreenCompleted(1)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Permissions & Privacy") },
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
                    text = "Help us help you",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Choose what you're comfortable sharing to improve your experience.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                PermissionCard(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    description = "Get reminders and progress updates to stay on track with your habits.",
                    isEnabled = notificationsEnabled,
                    onToggle = { notificationsEnabled = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PermissionCard(
                    icon = Icons.Default.Info,
                    title = "Usage Analytics",
                    description = "Help us improve the app by sharing anonymous usage data.",
                    isEnabled = analyticsConsent,
                    onToggle = { analyticsConsent = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PermissionCard(
                    icon = Icons.Default.Lock,
                    title = "Crash Reporting",
                    description = "Automatically send crash reports to help us fix bugs faster.",
                    isEnabled = crashReportingConsent,
                    onToggle = { crashReportingConsent = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "You can change these settings anytime in the app preferences.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Column {
                Button(
                    onClick = {
                        viewModel.updateConsentSettings(
                            analyticsConsent = analyticsConsent,
                            crashReportingConsent = crashReportingConsent,
                            marketingConsent = false, // Not requested in this screen
                            dataSharingConsent = analyticsConsent
                        )
                        onNext()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip for now")
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPermissionsScreenPreview() {
    SteadyMateTheme {
        OnboardingPermissionsScreen(
            viewModel = OnboardingViewModel(
                // Mock repository for preview
                object : com.steadymate.app.data.repository.OnboardingPreferencesRepository {
                    override fun getOnboardingPrefs() = kotlinx.coroutines.flow.flowOf(
                        com.steadymate.app.data.proto.OnboardingPrefs.getDefaultInstance()
                    )
                    override suspend fun updateTheme(theme: com.steadymate.app.data.proto.Theme) {}
                    override suspend fun updateColorPalette(colorPalette: com.steadymate.app.data.proto.Theme.ColorPalette) {}
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
                    override fun getColorPalette() = kotlinx.coroutines.flow.flowOf(
                        com.steadymate.app.data.proto.Theme.ColorPalette.BEAUTIFUL
                    )
                }
            ),
            onNext = {},
            onBack = {}
        )
    }
}
