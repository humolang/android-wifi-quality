package com.humolang.wifiless

import android.app.Application
import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.WifiCallback
import com.humolang.wifiless.data.repositories.RssiRepository

class WiFilessApplication : Application() {

    lateinit var wifiCallback: WifiCallback
    lateinit var rssiValue: RssiValue
    lateinit var rssiRepository: RssiRepository

    override fun onCreate() {
        super.onCreate()

        wifiCallback = WifiCallback(this)
        rssiValue = RssiValue(this)

        rssiRepository = RssiRepository(
            wifiCallback = wifiCallback,
            rssiValue = rssiValue
        )
    }
}