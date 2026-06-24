package com.nagarrakshak.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Map : Screen("map", "Safety Map")
    object Report : Screen("report", "Report Hazard")
    object Alerts : Screen("alerts", "Alerts")
    object Leaderboard : Screen("leaderboard", "Leaderboard")
    object Profile : Screen("profile", "Profile")
    object HazardDetail : Screen("hazard_detail/{hazardId}", "Hazard Details") {
        fun createRoute(hazardId: String) = "hazard_detail/$hazardId"
    }
}
