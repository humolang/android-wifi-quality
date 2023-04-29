package com.humolang.wifiless.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class RssiDataSource(
    private val refreshIntervalMs: Long = 100
) {

    private val _latestRssi: Flow<Int> = flow {
        while (true) {
            val rssi = Random(
                System.currentTimeMillis()
            ).nextInt(0, 101)

            emit(rssi)
            delay(refreshIntervalMs)
        }
    }

    val latestRssi: Flow<Int>
        get() = _latestRssi
}