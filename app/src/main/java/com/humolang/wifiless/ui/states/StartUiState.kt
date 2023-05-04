package com.humolang.wifiless.ui.states

data class StartUiState(
    val isWifiConnected: Boolean = false,
    val latestRssi: Int = 0,
    val rssiValues: ArrayDeque<Int> = ArrayDeque()
)