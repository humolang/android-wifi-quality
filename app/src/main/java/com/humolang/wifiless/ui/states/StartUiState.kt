package com.humolang.wifiless.ui.states

data class StartUiState(
    val isWifiConnected: Boolean = false,
    val dequeCapacity: Int,
    val latestRssi: Int = 0,
    val rssiHorizontalCapacity: Int,
    val maxRssi: Int,
    val rssiValues: ArrayDeque<Int> = ArrayDeque(),
    val latestSpeed: Int = 0,
    val linkSpeedHorizontalCapacity: Int,
    val maxLinkSpeed: Int,
    val speedValues: ArrayDeque<Int> = ArrayDeque(),
    val ipAddress: String = ""
)