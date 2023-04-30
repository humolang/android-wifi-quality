package com.humolang.wifiless.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class RssiRepository(
    private val wifiCallback: WifiCallback,
    private val rssiDataSource: RssiDataSource,
    private val refreshIntervalMs: Long = 100
) {

    val isWifiConnection: StateFlow<Boolean>
        get() = wifiCallback.isWifiConnection

    private val _latestRssi: Flow<Int> = flow {
        while (true) {
            val rssi = rssiDataSource.latestRssi
            // Log.d("RSSI", "rssi = $rssi")

            emit(rssi)
            delay(refreshIntervalMs)
        }
    }

    val latestRssi: Flow<Int>
        get() = _latestRssi
}