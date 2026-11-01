package com.smartkuyo.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartkuyo.app.ui.display.DisplayScreen
import com.smartkuyo.app.ui.display.DisplayViewModel
import com.smartkuyo.app.ui.settings.SettingsScreen
import com.smartkuyo.app.ui.setup.SetupScreen

@Composable
fun SmartKuyoNavHost() {
    val navController = rememberNavController()
    val displayViewModel: DisplayViewModel = hiltViewModel()
    val prefs by displayViewModel.preferences.collectAsState()

    val startDestination = if (prefs.isSetupComplete) {
        Screen.Display.route
    } else {
        Screen.Setup.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Display.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Display.route) {
            DisplayScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetToSetup = {
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
