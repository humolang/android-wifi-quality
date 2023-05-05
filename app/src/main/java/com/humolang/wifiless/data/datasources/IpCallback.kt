package com.humolang.wifiless.data.datasources

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import java.net.Inet4Address

class IpCallback(
    private val connectivityManager: ConnectivityManager,
    private val wifiRequest: NetworkRequest
) {

    private val _ipAddress = callbackFlow {
        val ipCallback = object : NetworkCallback() {

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)

                val linkAddresses = linkProperties.linkAddresses
                for (address in linkAddresses) {
                    try {
                        val ip4 = address.address as Inet4Address
                        val ipString = ip4.hostAddress ?: "no address"

                        trySendBlocking(ipString)
                            .onFailure { throwable ->
                                close(throwable)
                            }

                        break
                    } catch (exception: ClassCastException) {
                        continue
                    }
                }
            }
        }

        connectivityManager
            .registerNetworkCallback(
                wifiRequest,
                ipCallback
            )

        awaitClose {
            connectivityManager
                .unregisterNetworkCallback(
                    ipCallback
                )
        }
    }.buffer(Channel.CONFLATED)

    val ipAddress: Flow<String>
        get() = _ipAddress
}