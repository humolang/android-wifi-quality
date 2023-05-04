package com.humolang.wifiless.data.datasources

import android.content.Context
import android.net.wifi.WifiManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RssiValue(
    applicationContext: Context,
    private val refreshIntervalMs: Long = 100
) {

    private val wifiManager = applicationContext.getSystemService(
        Context.WIFI_SERVICE
    ) as WifiManager

    private val _rssi: Int
        get() {
            val wifiInfo = wifiManager
                .connectionInfo

            return wifiInfo.rssi
        }

    private val _latestRssi = flow {
        while (true) {
            emit(_rssi)
            delay(refreshIntervalMs)
        }
    }
    val latestRssi: Flow<Int>
        get() = _latestRssi
}