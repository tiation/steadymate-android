package com.steadymate.app.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.steadymate.app.ui.screens.onboarding.OnboardingWelcomeScreen
import com.steadymate.app.ui.screens.onboarding.OnboardingPermissionsScreen
import com.steadymate.app.ui.screens.onboarding.OnboardingPreferencesScreen
import com.steadymate.app.ui.screens.onboarding.OnboardingProfileSetupScreen
import com.steadymate.app.ui.screens.onboarding.OnboardingViewModel

/**
 * Onboarding navigation destinations
 */
object OnboardingDestinations {
    const val ONBOARDING_ROUTE = "onboarding"
    const val WELCOME = "welcome"
    const val PERMISSIONS = "permissions" 
    const val PREFERENCES = "preferences"
    const val PROFILE_SETUP = "profile_setup"
}

/**
 * Adds the onboarding navigation graph to the NavGraphBuilder
 */
fun NavGraphBuilder.onboardingNavigation(
    navController: NavController,
    onOnboardingComplete: () -> Unit
) {
    navigation(
        startDestination = OnboardingDestinations.WELCOME,
        route = OnboardingDestinations.ONBOARDING_ROUTE
    ) {
        composable(OnboardingDestinations.WELCOME) {
            OnboardingWelcomeScreen(
                viewModel = hiltViewModel<OnboardingViewModel>(),
                onNext = { 
                    navController.navigate(OnboardingDestinations.PERMISSIONS)
                },
                onSkip = onOnboardingComplete
            )
        }
        
        composable(OnboardingDestinations.PERMISSIONS) {
            OnboardingPermissionsScreen(
                viewModel = hiltViewModel<OnboardingViewModel>(),
                onNext = { 
                    navController.navigate(OnboardingDestinations.PREFERENCES)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(OnboardingDestinations.PREFERENCES) {
            OnboardingPreferencesScreen(
                viewModel = hiltViewModel<OnboardingViewModel>(),
                onNext = { 
                    navController.navigate(OnboardingDestinations.PROFILE_SETUP)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(OnboardingDestinations.PROFILE_SETUP) {
            OnboardingProfileSetupScreen(
                viewModel = hiltViewModel<OnboardingViewModel>(),
                onComplete = onOnboardingComplete,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
