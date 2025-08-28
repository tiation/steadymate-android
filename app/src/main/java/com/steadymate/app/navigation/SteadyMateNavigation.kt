package com.steadymate.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
// import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.steadymate.app.ui.screens.habits.HabitsScreen
// Import actual screen implementations
import com.steadymate.app.ui.screens.HomeScreen
import com.steadymate.app.ui.screens.checkin.CheckInScreen
import com.steadymate.app.ui.screens.crisis.CrisisScreen
import com.steadymate.app.ui.screens.insights.InsightsScreen
import com.steadymate.app.ui.screens.settings.SettingsScreen
import com.steadymate.app.ui.screens.tools.ToolsScreen
import com.steadymate.app.ui.screens.cbt.CBTReframeScreen
import com.steadymate.app.ui.screens.cbt.CBTWorryTimerScreen
import com.steadymate.app.ui.screens.cbt.CBTMicroWinsScreen

/**
 * Main navigation composable for SteadyMate app
 * Includes bottom navigation bar and NavHost with all destinations
 */
@Composable
fun SteadyMateNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            // Only show bottom bar for main destinations, not for crisis overlay
            val currentRoute = currentDestination?.route
            val shouldShowBottomBar = currentRoute in listOf(
                "home", "tools", "habits", "insights", "settings"
            )

            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavigationItems.forEach { item ->
                        val routeName = when (item.route) {
                            SteadyRoute.Home -> "home"
                            SteadyRoute.Tools -> "tools"
                            SteadyRoute.Habits -> "habits"
                            SteadyRoute.Insights -> "insights"
                            SteadyRoute.Settings -> "settings"
                            else -> ""
                        }
                        val isSelected = currentRoute == routeName

                        NavigationBarItem(
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.badgeCount != null) {
                                            Badge {
                                                Text(item.badgeCount.toString())
                                            }
                                        } else if (item.hasNews) {
                                            Badge()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) {
                                            item.selectedIcon
                                        } else {
                                            item.unselectedIcon
                                        },
                                        contentDescription = item.title
                                    )
                                }
                            },
                            label = {
                                Text(item.title)
                            },
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    navController.navigate(routeName) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        SteadyMateNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * NavHost containing all navigation destinations
 */
@Composable
private fun SteadyMateNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        // Main habits screen - WORKING
        composable("habits") {
            HabitsScreen()
        }
        
        // Actual screen implementations
        composable("home") {
            HomeScreen(
                onNavigateToCheckIn = { navController.navigate("checkin") },
                onNavigateToTools = { navController.navigate("tools") },
                onNavigateToCrisis = { navController.navigate("crisis") },
                onNavigateToCBT = { cbtType -> 
                    when (cbtType) {
                        "reframe" -> navController.navigate("cbt_reframe")
                        "worry_timer" -> navController.navigate("cbt_worry_timer")
                        "micro_wins" -> navController.navigate("cbt_micro_wins")
                    }
                }
            )
        }
        
        composable("tools") {
            ToolsScreen(
                onNavigateToCBT = { cbtType ->
                    when (cbtType) {
                        "reframe" -> navController.navigate("cbt_reframe")
                        "worry_timer" -> navController.navigate("cbt_worry_timer")
                        "micro_wins" -> navController.navigate("cbt_micro_wins")
                    }
                }
            )
        }
        
        composable("insights") {
            InsightsScreen()
        }
        
        composable("settings") {
            SettingsScreen()
        }
        
        // Additional functional screens
        composable("crisis") {
            CrisisScreen()
        }
        
        composable("checkin") {
            CheckInScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // CBT Tool Screens - Coming Soon Placeholders
        composable("cbt_reframe") {
            ComingSoonScreen(
                title = "Thought Reframing",
                description = "Challenge negative thoughts with evidence-based CBT techniques.",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("cbt_worry_timer") {
            ComingSoonScreen(
                title = "Worry Timer",
                description = "Manage anxiety by setting dedicated worry time.",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("cbt_micro_wins") {
            ComingSoonScreen(
                title = "Micro Wins",
                description = "Track daily achievements and practice gratitude.",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Future nested routes with arguments
        // composable("habit_detail/{habitId}") { backStackEntry ->
        //     val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
        //     HabitDetailScreen(habitId = habitId)
        // }
    }
}

/**
 * Extension function to navigate to crisis screen from anywhere
 */
fun NavHostController.navigateToCrisis() {
    this.navigate("crisis") {
        launchSingleTop = true
    }
}
