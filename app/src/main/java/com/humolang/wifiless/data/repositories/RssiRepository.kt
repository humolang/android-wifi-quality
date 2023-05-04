package com.humolang.wifiless.data.repositories

import android.util.Log
import com.humolang.wifiless.data.datasources.WifiCallback
import com.humolang.wifiless.data.datasources.RssiValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RssiRepository(
    private val wifiCallback: WifiCallback,
    private val rssiValue: RssiValue,
    private val refreshIntervalMs: Long = 1000
) {

    private val _isWifiConnected = wifiCallback.isWifiConnected
    val isWifiConnected
        get() = _isWifiConnected

    private val _latestRssi: Flow<Int> = flow {
        while (true) {
            val rssi = rssiValue.latestRssi
            Log.d("RSSI", "rssi = $rssi")

            emit(rssi)
            delay(refreshIntervalMs)
        }
    }

    val latestRssi: Flow<Int>
        get() = _latestRssi
}