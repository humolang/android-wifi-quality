package com.humolang.wifiless.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.humolang.wifiless.ui.screens.MappingScreen
import com.humolang.wifiless.ui.screens.StartScreen

@Composable
fun WiFilessNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = START_SCREEN_STRING
    ) {
        composable("start") {
            StartScreen(onNavigateToMap = {
                navController.navigate(MAP_SCREEN_STRING)
            })
        }
        composable(MAP_SCREEN_STRING) { MappingScreen() }
    }
}