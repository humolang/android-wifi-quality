package com.humolang.wifiless

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.humolang.wifiless.data.datasources.LinkSpeedValue
import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.WifiCallback
import com.humolang.wifiless.data.repositories.WifiParameters

class WiFilessApplication : Application() {

    lateinit var wifiParameters: WifiParameters

    override fun onCreate() {
        super.onCreate()

        val connectivityManager = getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val wifiCallback = WifiCallback(connectivityManager)

        val wifiManager = getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
        val rssiValue = RssiValue(wifiManager)
        val linkSpeedValue = LinkSpeedValue(wifiManager)

        wifiParameters = WifiParameters(
            wifiCallback = wifiCallback,
            rssiValue = rssiValue,
            linkSpeedValue = linkSpeedValue
        )
    }
}