package com.steadymate.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.steadymate.app.R
import com.steadymate.app.ui.theme.SteadyMateTheme

@Composable
fun OnboardingWelcomeScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        // Mark this screen as started when it's shown
        viewModel.markScreenCompleted(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // App logo or illustration
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "SteadyMate Logo",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Welcome to SteadyMate",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Your companion for building steady, lasting habits that transform your life one day at a time.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Features list
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                FeatureItem(
                    text = "📊 Track your progress with beautiful charts"
                )
                FeatureItem(
                    text = "🎯 Set personalized goals and reminders"
                )
                FeatureItem(
                    text = "🏆 Celebrate achievements and milestones"
                )
                FeatureItem(
                    text = "🧘 Build mindful, sustainable habits"
                )
            }
        }
        
        // Bottom buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Started")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip Setup")
            }
        }
    }
}

@Composable
private fun FeatureItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWelcomeScreenPreview() {
    SteadyMateTheme {
        OnboardingWelcomeScreen(
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
            onSkip = {}
        )
    }
}
