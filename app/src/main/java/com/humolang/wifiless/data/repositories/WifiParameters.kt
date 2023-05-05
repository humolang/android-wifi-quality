package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.IpCallback
import com.humolang.wifiless.data.datasources.LinkSpeedValue
import com.humolang.wifiless.data.datasources.WifiCallback
import com.humolang.wifiless.data.datasources.RssiValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.abs

class WifiParameters(
    wifiCallback: WifiCallback,
    ipCallback: IpCallback,
    rssiValue: RssiValue,
    linkSpeedValue: LinkSpeedValue,
    private val _dequeCapacity: Int = 120,
) {

    private val _isWifiConnected = wifiCallback.isWifiConnected
    val isWifiConnected: Flow<Boolean>
        get() = _isWifiConnected

    private val _latestRssi = rssiValue.latestRssi
    val latestRssi: Flow<Int>
        get() = _latestRssi

    val dequeCapacity: Int
        get() = _dequeCapacity

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

    private val _latestSpeed = linkSpeedValue.latestSpeed
    val latestSpeed: Flow<Int>
        get() = _latestSpeed

    private val speedDeque = ArrayDeque<Int>(_dequeCapacity)

    private val _speedValues = _latestSpeed.map { speed ->
        speedDeque.add(speed)

        if (speedDeque.size >_dequeCapacity) {
            speedDeque.removeFirst()
        }

        ArrayDeque(speedDeque)
    }
    val speedValues: Flow<ArrayDeque<Int>>
        get() = _speedValues

    private val _ipAddress = ipCallback.ipAddress
    val ipAddress: Flow<String>
        get() = _ipAddress
}