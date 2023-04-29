package com.humolang.wifiless

import android.app.Application
import com.humolang.wifiless.data.RssiDataSource
import com.humolang.wifiless.data.RssiRepository

class WiFilessApplication : Application() {

    lateinit var rssiRepository: RssiRepository

    override fun onCreate() {
        super.onCreate()

        rssiRepository = RssiRepository(
            rssiDataSource = RssiDataSource()
        )
    }
}