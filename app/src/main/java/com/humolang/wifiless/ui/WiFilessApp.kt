package com.humolang.wifiless.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.humolang.wifiless.ui.navigation.WiFilessNavHost
import com.humolang.wifiless.ui.theme.WiFilessTheme

@Composable
fun WiFilessApp(modifier: Modifier = Modifier) {
    WiFilessTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = modifier.fillMaxSize()
        ) {
            val navController = rememberNavController()
            WiFilessNavHost(navController = navController)
        }
    }
}