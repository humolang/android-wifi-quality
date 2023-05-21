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

                val isAndroidR = Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.R

                val linkAddresses = linkProperties.linkAddresses
                var ipString = UNKNOWN

                for (address in linkAddresses) {
                    try {
                        val ip4 = address.address as Inet4Address
                        ipString = ip4.hostAddress
                            ?: UNKNOWN

                        break
                    } catch (exception: ClassCastException) {
                        continue
                    }
                }

                val properties = WifiProperties(
                    ipAddress = ipString,

                    nat64Prefix = if (isAndroidR)
                        linkProperties.nat64Prefix
                            ?.address?.hostAddress ?: UNKNOWN
                    else "Requires Android 11",

                    interfaceName = linkProperties.interfaceName
                        ?: UNKNOWN,

                    dhcpServer = if (isAndroidR)
                        linkProperties.dhcpServerAddress
                            ?.hostAddress ?: UNKNOWN
                    else "Requires Android 11",

                    dnsServers = linkProperties.dnsServers
                        .map { address ->
                            address.hostAddress
                                ?: UNKNOWN
                        },

                    httpProxy = linkProperties.httpProxy
                        ?.host ?: UNKNOWN
                )

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
}