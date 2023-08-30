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

package com.humolang.wifiless.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.humolang.wifiless.data.datasources.DARK_THEME
import com.humolang.wifiless.data.datasources.SYSTEM_THEME
import com.humolang.wifiless.ui.navigation.WiFilessNavHost
import com.humolang.wifiless.ui.theme.WiFilessTheme
import com.humolang.wifiless.ui.viewmodels.SettingsViewModel

@Composable
fun WiFilessApp(
    settingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModel.Factory)
) {
    val theme by settingsViewModel
        .theme.collectAsStateWithLifecycle()
    val dynamicColor by settingsViewModel
        .dynamicColor.collectAsStateWithLifecycle()

    WiFilessTheme(
        darkTheme = if (theme == SYSTEM_THEME)
            isSystemInDarkTheme()
        else theme == DARK_THEME,

        dynamicColor = dynamicColor
    ) {
        val navController = rememberNavController()

        WiFilessNavHost(
            navController = navController
        )
    }
}