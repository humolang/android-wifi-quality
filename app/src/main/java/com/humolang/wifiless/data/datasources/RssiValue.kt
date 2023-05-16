package com.humolang.wifiless.data.datasources

import android.net.wifi.WifiManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RssiValue(
    private val wifiManager: WifiManager,
    private val refreshIntervalMs: Long = 100
) {

    val rssi: Int
        get() {
            val wifiInfo = wifiManager
                .connectionInfo

            return wifiInfo.rssi
        }

    private val _latestRssi = flow {
        while (true) {
            emit(rssi)
            delay(refreshIntervalMs)
        }
    }
    val latestRssi: Flow<Int>
        get() = _latestRssi
}