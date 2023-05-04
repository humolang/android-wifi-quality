package com.humolang.wifiless.data.datasources

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class WifiCallback(
    applicationContext: Context
) {

    private val _isWifiConnected = callbackFlow {
        val wifiCallback = object : NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                trySendBlocking(true)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }

            override fun onLost(network: Network) {
                super.onLost(network)

                trySendBlocking(false)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }
        }

        connectivityManager
            .registerNetworkCallback(
                wifiRequest,
                wifiCallback
            )

        awaitClose {
            connectivityManager
                .unregisterNetworkCallback(
                    wifiCallback
                )
        }
    }.buffer(Channel.CONFLATED)

    val isWifiConnected: Flow<Boolean>
        get() = _isWifiConnected

    private val connectivityManager = applicationContext.getSystemService(
        ConnectivityManager::class.java
    ) as ConnectivityManager

    private val wifiRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()
}