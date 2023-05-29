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
import java.net.Inet4Address
import java.net.Inet6Address

class PropertiesCallback(
    private val connectivityManager: ConnectivityManager,
    private val wifiRequest: NetworkRequest
) {

    private val _wifiProperties = callbackFlow {
        val propertiesCallback = object : NetworkCallback() {

            override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties
            ) {
                super.onLinkPropertiesChanged(
                    network,
                    linkProperties
                )

                val properties =
                    updateWifiProperties(linkProperties)

                trySendBlocking(properties)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }

            override fun onLost(network: Network) {
                super.onLost(network)

                val linkProperties = connectivityManager
                    .getLinkProperties(network)

                val properties = if (linkProperties != null) {
                    updateWifiProperties(linkProperties)
                } else {
                    WifiProperties()
                }

                trySendBlocking(properties)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }
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

    private fun updateWifiProperties(
        linkProperties: LinkProperties
    ): WifiProperties {
        val isAndroidR = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.R

        val linkAddresses = linkProperties.linkAddresses

        var ipv4String = UNKNOWN
        for (address in linkAddresses) {
            try {
                val ip4 = address.address as Inet4Address
                ipv4String = ip4.hostAddress ?: UNKNOWN

                break
            } catch (exception: ClassCastException) {
                continue
            }
        }

        var ipv6String = UNKNOWN
        for (address in linkAddresses) {
            try {
                val ip6 = address.address as Inet6Address
                ipv6String = ip6.hostAddress ?: UNKNOWN

                break
            } catch (exception: ClassCastException) {
                continue
            }
        }

        val properties = WifiProperties(
            ipv4Address = ipv4String,
            ipv6Address = ipv6String,

            interfaceName = linkProperties
                .interfaceName ?: UNKNOWN,

            dhcpServer = if (isAndroidR)
                linkProperties.dhcpServerAddress
                    ?.hostAddress ?: UNKNOWN
            else "Requires Android 11",

            dnsServer = linkProperties
                .dnsServers.firstOrNull()
                ?.hostAddress ?: UNKNOWN,
        )

        return properties
    }
}