package com.humolang.wifiless.ui.states

data class StartUiState(
    val isWifiConnected: Boolean = false,
    val dequeCapacity: Int,
    val latestRssi: Int = 0,
    val rssiHorizontalCapacity: Int,
    val minRssi: Int,
    val rssiValues: ArrayDeque<Int> = ArrayDeque(),
    val latestSpeed: Int = 0,
    val linkSpeedUnits: String,
    val linkSpeedHorizontalCapacity: Int,
    val maxLinkSpeed: Int,
    val speedValues: ArrayDeque<Int> = ArrayDeque(),
    val ipAddress: String = ""
)