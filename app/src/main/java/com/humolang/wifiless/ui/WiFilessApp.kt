package com.humolang.wifiless.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.humolang.wifiless.ui.navigation.WiFilessNavHost
import com.humolang.wifiless.ui.theme.WiFilessTheme

@Composable
fun WiFilessApp(modifier: Modifier = Modifier) {
    WiFilessTheme {
        val navController = rememberNavController()
        WiFilessNavHost(navController = navController)
    }
}