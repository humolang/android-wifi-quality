package com.humolang.wifiless.data

import android.content.Context
import android.net.wifi.WifiManager

class RssiDataSource(
    private val applicationContext: Context
) {

    val latestRssi: Int
        get() {
            val wifiManager = applicationContext.getSystemService(
                Context.WIFI_SERVICE
            ) as WifiManager
            val wifiInfo = wifiManager.connectionInfo

            return wifiInfo.rssi
        }
}