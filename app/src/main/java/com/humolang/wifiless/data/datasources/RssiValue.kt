package com.humolang.wifiless.data.datasources

import android.net.wifi.WifiManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RssiValue(
    private val wifiManager: WifiManager,
    private val _refreshIntervalMs: Long = 100L
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
            delay(_refreshIntervalMs)
        }
    }
    val latestRssi: Flow<Int>
        get() = _latestRssi

    private var _minRssi = -127
    val minRssi: Int
        get() = _minRssi

    val rssiRefreshIntervalMs: Long
        get() = _refreshIntervalMs

    fun updateMinRssi(newValue: Int) {
        _minRssi = newValue
    }
}