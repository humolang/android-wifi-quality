package com.humolang.wifiless.data.datasources

import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LinkSpeedValue(
    private val wifiManager: WifiManager,
    private val _refreshIntervalMs: Long = 100L
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
            delay(_refreshIntervalMs)
        }
    }
    val latestSpeed: Flow<Int>
        get() = _latestSpeed

    val linkSpeedUnits: String
        get() = WifiInfo.LINK_SPEED_UNITS

    private var _maxLinkSpeed = 144
    val maxLinkSpeed: Int
        get() = _maxLinkSpeed

    val linkSpeedRefreshIntervalMs: Long
        get() = _refreshIntervalMs

    fun updateMaxLinkSpeed(newValue: Int) {
        _maxLinkSpeed = newValue
    }
}