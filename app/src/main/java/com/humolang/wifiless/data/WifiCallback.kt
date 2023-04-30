package com.humolang.wifiless.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WifiCallback(
    applicationContext: Context
) {

    private val connectivityManager = applicationContext.getSystemService(
        ConnectivityManager::class.java
    ) as ConnectivityManager

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _isWifiConnection.value = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _isWifiConnection.value = false
        }
    }

    private val _isWifiConnection = MutableStateFlow(false)
    val isWifiConnection = _isWifiConnection.asStateFlow()

    fun registerNetworkCallback() {
        connectivityManager.registerNetworkCallback(
            networkRequest,
            networkCallback
        )
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(
            networkCallback
        )
    }
}