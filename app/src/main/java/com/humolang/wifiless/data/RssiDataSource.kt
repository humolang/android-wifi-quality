package com.humolang.wifiless.data

import android.content.Context
import android.net.wifi.WifiManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RssiDataSource(
    private val applicationContext: Context,
    private val refreshIntervalMs: Long = 100
) {

    private val _latestRssi: Flow<Int> = flow {
        while (true) {
//            val rssi = Random(
//                System.currentTimeMillis()
//            ).nextInt(0, 101)

            val wifiManager = applicationContext.getSystemService(
                Context.WIFI_SERVICE
            ) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val rssi = wifiInfo.rssi

            emit(rssi)
            delay(refreshIntervalMs)
        }
    }

    val latestRssi: Flow<Int>
        get() = _latestRssi
}