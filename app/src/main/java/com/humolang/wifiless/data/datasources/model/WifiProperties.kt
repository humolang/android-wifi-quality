package com.humolang.wifiless.data.datasources.model

import com.humolang.wifiless.data.datasources.UNKNOWN

data class WifiProperties(
    val ipv4Address: String = UNKNOWN,
    val ipv6Address: String = UNKNOWN,
    val interfaceName: String = UNKNOWN,
    val dhcpServer: String = UNKNOWN,
    val dnsServer: String = UNKNOWN
)
