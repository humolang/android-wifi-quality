package com.humolang.wifiless

import android.app.Application
import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.WifiCallback
import com.humolang.wifiless.data.repositories.WifiParameters

class WiFilessApplication : Application() {

    lateinit var wifiCallback: WifiCallback
    lateinit var rssiValue: RssiValue
    lateinit var wifiParameters: WifiParameters

    override fun onCreate() {
        super.onCreate()

        wifiCallback = WifiCallback(this)
        rssiValue = RssiValue(this)

        wifiParameters = WifiParameters(
            wifiCallback = wifiCallback,
            rssiValue = rssiValue
        )
    }
}