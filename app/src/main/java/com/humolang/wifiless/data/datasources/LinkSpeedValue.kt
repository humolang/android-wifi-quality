package com.humolang.wifiless.data.datasources

import android.net.wifi.WifiManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LinkSpeedValue(
    private val wifiManager: WifiManager,
    private val refreshIntervalMs: Long = 100
) {

    private val _linkSpeed: Int
        get() {
            val wifiInfo = wifiManager
                .connectionInfo

            return wifiInfo.linkSpeed
        }

    private val _latestSpeed = flow {
        while (true) {
            emit(_linkSpeed)
            delay(refreshIntervalMs)
        }
    }
    val latestSpeed: Flow<Int>
        get() = _latestSpeed
}