/*
 * Copyright (c) 2023  humolang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    private val _latestLinkSpeed = flow {
        while (true) {
            emit(_linkSpeed)
            delay(_refreshIntervalMs)
        }
    }
    val latestLinkSpeed: Flow<Int>
        get() = _latestLinkSpeed

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