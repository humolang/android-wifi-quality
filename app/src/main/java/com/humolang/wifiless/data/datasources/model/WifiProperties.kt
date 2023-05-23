package com.humolang.wifiless.data.datasources.model

import com.humolang.wifiless.data.datasources.UNKNOWN

data class WifiProperties(
    val ipAddress: String = UNKNOWN,
    val nat64Prefix: String = UNKNOWN,
    val interfaceName: String = UNKNOWN,
    val dhcpServer: String = UNKNOWN,
    val dnsServers: List<String> = emptyList(),
    val httpProxy: String = UNKNOWN
)
