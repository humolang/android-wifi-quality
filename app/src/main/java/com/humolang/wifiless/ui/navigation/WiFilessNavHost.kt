package com.humolang.wifiless.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.humolang.wifiless.ui.screens.MappingScreen
import com.humolang.wifiless.ui.screens.PlanningScreen
import com.humolang.wifiless.ui.screens.StartScreen

@Composable
fun WiFilessNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = START_SCREEN_STRING
    ) {
        composable(START_SCREEN_STRING) {
            StartScreen(
                onNavigateToPlan = {
                    navController.navigate(PLANNING_SCREEN_STRING)
                },
                onNavigateToMap = {
                    navController.navigate(MAPPING_SCREEN_STRING)
                }
            )
        }
        composable(PLANNING_SCREEN_STRING) { PlanningScreen() }
        composable(MAPPING_SCREEN_STRING) { MappingScreen() }
    }
}