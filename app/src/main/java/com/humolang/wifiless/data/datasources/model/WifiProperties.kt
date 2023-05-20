package com.humolang.wifiless.data.datasources.model

data class WifiProperties(
    val dhcpServerAddress: String = "",
    val dnsServers: List<String> = emptyList(),
    val domains: String = "",
    val httpProxy: String = "",
    val interfaceName: String = "",
    val linkAddresses: List<String> = emptyList(),
    val mtu: Int = -1,
    val nat64Prefix: String = "",
    val privateDnsServerName: String = "",
    val routes: List<String> = emptyList(),
    val isPrivateDnsActive: Boolean = false,
    val isWakeOnLanSupported: Boolean = false
)
