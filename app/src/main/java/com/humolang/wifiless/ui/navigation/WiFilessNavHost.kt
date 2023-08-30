/*
 * Copyright (c) 2023  humolang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.humolang.wifiless.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.humolang.wifiless.data.datasources.DEFAULT_HEAT_ID
import com.humolang.wifiless.ui.screens.AboutScreen
import com.humolang.wifiless.ui.screens.HeatsScreen
import com.humolang.wifiless.ui.screens.MappingScreen
import com.humolang.wifiless.ui.screens.PlanningScreen
import com.humolang.wifiless.ui.screens.SettingsScreen
import com.humolang.wifiless.ui.screens.StartScreen

@Composable
fun WiFilessNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = START_SCREEN_STRING
    ) {

        composable(START_SCREEN_STRING) {
            StartScreen(
                navigateToSettings = {
                    navController.navigate(
                        route = SETTINGS_SCREEN_STRING
                    )
                },
                navigateToAbout = {
                    navController.navigate(
                        route = ABOUT_SCREEN_STRING
                    )
                },
                navigateToPlanning = { navArg ->
                    navController.navigate(
                        route = "$PLANNING_SCREEN_STRING/$navArg"
                    )
                },
                navigateToHeats = {
                    navController.navigate(
                        route = HEATS_SCREEN_STRING
                    )
                }
            )
        }

        composable(SETTINGS_SCREEN_STRING) {
            SettingsScreen(
                popBackStack = {
                    navController.popBackStack()
                }
            )
        }

        composable(ABOUT_SCREEN_STRING) {
            AboutScreen(
                popBackStack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = PLANNING_SCREEN_WITH_ARG,
            arguments = listOf(
                navArgument(HEAT_ID_ARG) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val heatId = backStackEntry
                .arguments?.getLong(
                    HEAT_ID_ARG,
                    DEFAULT_HEAT_ID
                )

            if (heatId != null) {
                PlanningScreen(
                    heatId = heatId,
                    popBackStack = { navController.popBackStack() },
                    navigateToMapping = { navArg ->
                        navController.navigate(
                            route = "$MAPPING_SCREEN_STRING/$navArg"
                        )
                    }
                )
            }
        }

        composable(
            route = MAPPING_SCREEN_WITH_ARG,
            arguments = listOf(
                navArgument(HEAT_ID_ARG) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val heatId = backStackEntry
                .arguments?.getLong(HEAT_ID_ARG)

            if (heatId != null) {
                MappingScreen(
                    heatId = heatId,
                    popBackStack = { navController.popBackStack() },
                    navigateToStart = {
                        navController.navigate(
                            route = START_SCREEN_STRING
                        )
                    }
                )
            }
        }

        composable(HEATS_SCREEN_STRING) {
            HeatsScreen(
                popBackStack = { navController.popBackStack() },
                navigateToPlanning = { navArg ->
                    navController.navigate(
                        route = "$PLANNING_SCREEN_STRING/$navArg"
                    )
                },
                navigateToMapping = { navArg ->
                    navController.navigate(
                        route = "$MAPPING_SCREEN_STRING/$navArg"
                    )
                }
            )
        }
    }
}