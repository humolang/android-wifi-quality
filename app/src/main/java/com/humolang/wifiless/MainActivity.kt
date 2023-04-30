package com.humolang.wifiless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.humolang.wifiless.data.WifiCallback
import com.humolang.wifiless.ui.WiFilessApp

class MainActivity : ComponentActivity() {

    private lateinit var wifiCallback: WifiCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiCallback = (application as WiFilessApplication)
            .wifiCallback

        setContent { WiFilessApp() }
    }

    override fun onResume() {
        super.onResume()
        wifiCallback
            .registerNetworkCallback()
    }

    override fun onPause() {
        super.onPause()
        wifiCallback
            .unregisterNetworkCallback()
    }
}