package com.humolang.wifiless

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import com.humolang.wifiless.data.datasources.IpCallback
import com.humolang.wifiless.data.datasources.LinkSpeedValue
import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.WifiCallback
import com.humolang.wifiless.data.repositories.MappingTool
import com.humolang.wifiless.data.repositories.WifiParameters

class WiFilessApplication : Application() {

    lateinit var wifiParameters: WifiParameters
    lateinit var mappingTool: MappingTool

    override fun onCreate() {
        super.onCreate()

        val connectivityManager = getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val wifiRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val wifiCallback = WifiCallback(
            connectivityManager,
            wifiRequest
        )
        val ipCallback = IpCallback(
            connectivityManager,
            wifiRequest
        )

        val wifiManager = getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
        val rssiValue = RssiValue(wifiManager)
        val linkSpeedValue = LinkSpeedValue(wifiManager)

        wifiParameters = WifiParameters(
            wifiCallback = wifiCallback,
            ipCallback = ipCallback,
            rssiValue = rssiValue,
            linkSpeedValue = linkSpeedValue
        )

        mappingTool = MappingTool()
    }
}