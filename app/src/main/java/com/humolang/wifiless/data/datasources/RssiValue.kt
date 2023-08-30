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