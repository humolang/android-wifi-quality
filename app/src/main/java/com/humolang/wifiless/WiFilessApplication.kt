package com.humolang.wifiless

import android.app.Application
import com.humolang.wifiless.data.RssiDataSource
import com.humolang.wifiless.data.RssiRepository
import com.humolang.wifiless.data.WifiCallback

class WiFilessApplication : Application() {

    lateinit var wifiCallback: WifiCallback
    lateinit var rssiDataSource: RssiDataSource
    lateinit var rssiRepository: RssiRepository

    override fun onCreate() {
        super.onCreate()

        wifiCallback = WifiCallback(this)
        rssiDataSource = RssiDataSource(this)

        rssiRepository = RssiRepository(
            wifiCallback = wifiCallback,
            rssiDataSource = rssiDataSource
        )
    }
}