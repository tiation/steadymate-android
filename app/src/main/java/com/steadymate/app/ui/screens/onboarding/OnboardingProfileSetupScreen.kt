package com.steadymate.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steadymate.app.ui.theme.SteadyMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingProfileSetupScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayName by remember { mutableStateOf("") }
    var goalStatement by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        // Mark this screen as completed when it's shown
        viewModel.markScreenCompleted(3)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Almost Done!") },
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
                // Success icon
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "You're all set up!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Let's add a few personal touches to make your journey uniquely yours.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Display Name Field
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    placeholder = { Text("How should we call you?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Goal Statement Field
                OutlinedTextField(
                    value = goalStatement,
                    onValueChange = { goalStatement = it },
                    label = { Text("Your Goal (Optional)") },
                    placeholder = { Text("What habit would you like to build?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    supportingText = {
                        Text("e.g., \"I want to exercise for 30 minutes daily\" or \"Read 20 pages every day\"")
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Fun facts section
                Text(
                    text = "💡 Fun Fact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Studies show that it takes an average of 66 days to form a new habit. With SteadyMate, you'll track your progress and celebrate every milestone along the way!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column {
                Button(
                    onClick = {
                        isLoading = true
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Setting up..." else "Complete Setup")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Skip for now")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "You can update your profile anytime in settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingProfileSetupScreenPreview() {
    SteadyMateTheme {
        OnboardingProfileSetupScreen(
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
            onComplete = {},
            onBack = {}
        )
    }
}
