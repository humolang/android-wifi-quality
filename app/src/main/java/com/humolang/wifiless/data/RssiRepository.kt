package com.humolang.wifiless.data

import kotlinx.coroutines.flow.Flow

class RssiRepository(
    private val rssiDataSource: RssiDataSource
) {

    val latestRssi: Flow<Int>
        get() = rssiDataSource.latestRssi
}