package com.humolang.wifiless.data.datasources

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import com.humolang.wifiless.data.datasources.model.WifiProperties
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PropertiesCallback(
    private val connectivityManager: ConnectivityManager,
    private val wifiRequest: NetworkRequest
) {

    private val _wifiProperties = callbackFlow {
        val propertiesCallback = object : NetworkCallback() {

//            override fun onAvailable(network: Network) {
//                super.onAvailable(network)
//            }
//
//            override fun onLosing(
//                network: Network,
//                maxMsToLive: Int
//            ) {
//                super.onLosing(network, maxMsToLive)
//            }
//
//            override fun onLost(network: Network) {
//                super.onLost(network)
//            }
//
//            override fun onUnavailable() {
//                super.onUnavailable()
//            }
//
//            override fun onCapabilitiesChanged(
//                network: Network,
//                networkCapabilities: NetworkCapabilities
//            ) {
//                super.onCapabilitiesChanged(
//                    network,
//                    networkCapabilities
//                )
//            }

            override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties
            ) {
                super.onLinkPropertiesChanged(
                    network,
                    linkProperties
                )

                val isAndroidP = Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.P
                val isAndroidQ = Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.Q
                val isAndroidR = Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.R

                val properties = WifiProperties(
                    dhcpServerAddress = if (isAndroidR)
                        linkProperties.dhcpServerAddress?.toString() ?: "dhcpServerAddress"
                    else "",

                    dnsServers = linkProperties.dnsServers.map { it.toString() },
                    domains = linkProperties.domains ?: "domains",
                    httpProxy = linkProperties.httpProxy?.toString() ?: "httpProxy",
                    interfaceName = linkProperties.interfaceName ?: "interfaceName",
                    linkAddresses = linkProperties.linkAddresses.map { it.toString() },
                    mtu = if (isAndroidQ)
                        linkProperties.mtu
                    else -1,

                    nat64Prefix = if (isAndroidR)
                        linkProperties.nat64Prefix?.toString() ?: "nat64Prefix"
                    else "nat64Prefix",

                    privateDnsServerName = if (isAndroidP)
                        linkProperties.privateDnsServerName ?: "privateDnsServerName"
                    else "privateDnsServerName",

                    routes = linkProperties.routes.map { it.toString() },
                    isPrivateDnsActive = if (isAndroidP)
                        linkProperties.isPrivateDnsActive
                    else false,

                    isWakeOnLanSupported = if (isAndroidR)
                        linkProperties.isWakeOnLanSupported
                    else false
                )

                trySendBlocking(properties)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }

//            override fun onBlockedStatusChanged(
//                network: Network,
//                blocked: Boolean
//            ) {
//                super.onBlockedStatusChanged(
//                    network,
//                    blocked
//                )
//            }
        }

        connectivityManager
            .registerNetworkCallback(
                wifiRequest,
                propertiesCallback
            )

        awaitClose {
            connectivityManager
                .unregisterNetworkCallback(
                    propertiesCallback
                )
        }
    }

    val wifiProperties: Flow<WifiProperties>
        get() = _wifiProperties
}