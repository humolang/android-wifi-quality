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
import com.humolang.wifiless.data.repositories.WifiParameters

class WiFilessApplication : Application() {

    lateinit var wifiParameters: WifiParameters
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
            heatDao = database.heatDao()
        )
    }
}