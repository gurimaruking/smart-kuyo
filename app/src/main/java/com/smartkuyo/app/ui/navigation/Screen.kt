package com.smartkuyo.app.ui.navigation

sealed class Screen(val route: String) {
    data object Setup : Screen("setup")
    data object Display : Screen("display")
    data object Settings : Screen("settings")
}
