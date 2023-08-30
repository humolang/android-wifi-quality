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

package com.humolang.wifiless

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import com.humolang.wifiless.data.datasources.CapabilitiesCallback
import com.humolang.wifiless.data.datasources.LinkSpeedValue
import com.humolang.wifiless.data.datasources.PropertiesCallback
import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.db.MappingDatabase
import com.humolang.wifiless.data.repositories.HeatsRepository
import com.humolang.wifiless.data.repositories.MappingTool
import com.humolang.wifiless.data.repositories.PlanningTool
import com.humolang.wifiless.data.repositories.SettingsRepository
import com.humolang.wifiless.data.repositories.WifiParameters

class WiFilessApplication : Application() {

    lateinit var wifiParameters: WifiParameters
    lateinit var settingsRepository: SettingsRepository
    lateinit var planningTool: PlanningTool
    lateinit var mappingTool: MappingTool
    lateinit var heatsRepository: HeatsRepository

    override fun onCreate() {
        super.onCreate()

        val connectivityManager = getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val wifiManager = getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager

        val wifiRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val rssiValue = RssiValue(wifiManager)
        val linkSpeedValue = LinkSpeedValue(wifiManager)
        val capabilitiesCallback = CapabilitiesCallback(
            connectivityManager = connectivityManager,
            wifiManager = wifiManager,
            wifiRequest = wifiRequest
        )
        val propertiesCallback = PropertiesCallback(
            connectivityManager = connectivityManager,
            wifiRequest = wifiRequest
        )

        wifiParameters = WifiParameters(
            rssiValue = rssiValue,
            linkSpeedValue = linkSpeedValue,
            capabilitiesCallback = capabilitiesCallback,
            propertiesCallback = propertiesCallback
        )

        settingsRepository = SettingsRepository(
            context = this
        )

        val database = MappingDatabase
            .getDatabase(this)

        planningTool = PlanningTool(
            heatDao = database.heatDao(),
            columnDao = database.columnDao(),
            blockDao = database.blockDao()
        )

        mappingTool = MappingTool(
            heatDao = database.heatDao(),
            columnDao = database.columnDao(),
            blockDao = database.blockDao(),
            rssiValue = rssiValue
        )

        heatsRepository = HeatsRepository(
            heatDao = database.heatDao(),
            columnDao = database.columnDao()
        )
    }
}