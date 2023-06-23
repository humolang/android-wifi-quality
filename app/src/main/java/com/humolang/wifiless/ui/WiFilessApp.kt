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