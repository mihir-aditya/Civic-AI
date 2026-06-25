package com.nagarrakshak.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nagarrakshak.ui.screens.*

@Composable
fun NagarRakshakNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToReport = { navController.navigate(Screen.Report.route) },
                onNavigateToDetail = { hazardId -> navController.navigate(Screen.HazardDetail.createRoute(hazardId)) },
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToAlerts = { navController.navigate(Screen.Alerts.route) }
            )
        }
        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToDetail = { hazardId -> navController.navigate(Screen.HazardDetail.createRoute(hazardId)) }
            )
        }
        composable(Screen.Report.route) {
            ReportScreen(
                onReportSubmitted = { navController.popBackStack() }
            )
        }
        composable(Screen.Alerts.route) {
            AlertsScreen(
                onNavigateToDetail = { hazardId -> navController.navigate(Screen.HazardDetail.createRoute(hazardId)) }
            )
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToDetail = { hazardId -> navController.navigate(Screen.HazardDetail.createRoute(hazardId)) },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.HazardDetail.route,
            arguments = listOf(navArgument("hazardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val hazardId = backStackEntry.arguments?.getString("hazardId") ?: ""
            DetailScreen(
                hazardId = hazardId,
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}
