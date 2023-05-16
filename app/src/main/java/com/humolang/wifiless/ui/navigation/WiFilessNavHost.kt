package com.humolang.wifiless.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.humolang.wifiless.ui.screens.HeatsScreen
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
                navigateToPlanning = {
                    navController.navigate(PLANNING_SCREEN_STRING)
                },
                navigateToHeats = {
                    navController.navigate(HEATS_SCREEN_STRING)
                }
            )
        }
        composable(PLANNING_SCREEN_STRING) {
            PlanningScreen(
                onCancelClicked = { navController.popBackStack() },
                navigateToMapping = { heatId ->
                    navController.navigate("$MAPPING_SCREEN_STRING/$heatId")
                }
            )
        }
        composable(
            route = MAPPING_SCREEN_WITH_ARG,
            arguments = listOf(
                navArgument(HEAT_ID_ARG) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val heatId = backStackEntry
                .arguments?.getInt(HEAT_ID_ARG)

            if (heatId != null) {
                MappingScreen(
                    heatId = heatId,
                    navigateToStart = {
                        navController.navigate(START_SCREEN_STRING) {
                            popUpTo(START_SCREEN_STRING) { inclusive = true }
                        }
                    }
                )
            }
        }
        composable(HEATS_SCREEN_STRING) {
            HeatsScreen(
                navigateToMapping = { heatId ->
                    navController.navigate("$MAPPING_SCREEN_STRING/$heatId")
                }
            )
        }
    }
}