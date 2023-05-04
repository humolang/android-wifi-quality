package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.WifiCallback
import com.humolang.wifiless.data.datasources.RssiValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.abs

class WifiParameters(
    wifiCallback: WifiCallback,
    rssiValue: RssiValue,
    private val _dequeCapacity: Int = 120,
) {

    private val _isWifiConnected = wifiCallback.isWifiConnected
    val isWifiConnected: Flow<Boolean>
        get() = _isWifiConnected

    private val _latestRssi = rssiValue.latestRssi
    val latestRssi: Flow<Int>
        get() = _latestRssi

    private val rssiDeque = ArrayDeque<Int>(_dequeCapacity)

    private val _rssiValues = _latestRssi.map { rssi ->
        rssiDeque.add(abs(rssi))

        if (rssiDeque.size > _dequeCapacity) {
            rssiDeque.removeFirst()
        }

        ArrayDeque(rssiDeque)
    }
    val rssiValues: Flow<ArrayDeque<Int>>
        get() = _rssiValues

    val dequeCapacity: Int
        get() = _dequeCapacity
}