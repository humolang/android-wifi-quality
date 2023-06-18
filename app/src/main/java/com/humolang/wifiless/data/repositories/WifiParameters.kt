package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.CapabilitiesCallback
import com.humolang.wifiless.data.datasources.LinkSpeedValue
import com.humolang.wifiless.data.datasources.PropertiesCallback
import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.model.WifiCapabilities
import com.humolang.wifiless.data.datasources.model.WifiProperties
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WifiParameters(
    private val rssiValue: RssiValue,
    private val linkSpeedValue: LinkSpeedValue,
    capabilitiesCallback: CapabilitiesCallback,
    propertiesCallback: PropertiesCallback,
    private val _dequeCapacity: Int = 600,
) {

    private val _latestRssi = rssiValue.latestRssi
    val latestRssi: Flow<Int>
        get() = _latestRssi

    val minRssi: Int
        get() = rssiValue.minRssi

    val dequeCapacity: Int
        get() = _dequeCapacity

    val rssiHorizontalCapacity: Int
        get() = (rssiValue.rssiRefreshIntervalMs
                * _dequeCapacity).toInt() / 1000

    private val rssiDeque = ArrayDeque<Int>(_dequeCapacity)

    private val _rssiValues = _latestRssi
        .map { rssi ->
            rssiDeque.addFirst(rssi)

            if (rssiDeque.size > _dequeCapacity) {
                rssiDeque.removeLast()
            }

            ArrayDeque(rssiDeque)
        }
    val rssiValues: Flow<ArrayDeque<Int>>
        get() = _rssiValues

    private val _latestLinkSpeed = linkSpeedValue.latestLinkSpeed
    val latestLinkSpeed: Flow<Int>
        get() = _latestLinkSpeed

    val maxLinkSpeed: Int
        get() = linkSpeedValue.maxLinkSpeed

    val linkSpeedUnits: String
        get() = linkSpeedValue.linkSpeedUnits

    val linkSpeedHorizontalCapacity: Int
        get() = (linkSpeedValue.linkSpeedRefreshIntervalMs
                * _dequeCapacity).toInt() / 1000

    private val linkSpeedDeque = ArrayDeque<Int>(_dequeCapacity)

    private val _linkSpeedValues = _latestLinkSpeed
        .map { linkSpeed ->
            linkSpeedDeque.addFirst(linkSpeed)

            if (linkSpeedDeque.size > _dequeCapacity) {
                linkSpeedDeque.removeLast()
            }

            ArrayDeque(linkSpeedDeque)
        }
    val linkSpeedValues: Flow<ArrayDeque<Int>>
        get() = _linkSpeedValues

    private val _wifiCapabilities =
        capabilitiesCallback.wifiCapabilities
    val wifiCapabilities: Flow<WifiCapabilities>
        get() = _wifiCapabilities

    private val _wifiProperties =
        propertiesCallback.wifiProperties
    val wifiProperties: Flow<WifiProperties>
        get() = _wifiProperties

    fun updateMinRssi(newValue: Int) {
        rssiValue.updateMinRssi(newValue)
    }

    fun updateMaxLinkSpeed(newValue: Int) {
        linkSpeedValue.updateMaxLinkSpeed(newValue)
    }
}