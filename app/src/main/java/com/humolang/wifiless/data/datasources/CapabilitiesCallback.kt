package com.humolang.wifiless.data.datasources

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.humolang.wifiless.data.datasources.model.WifiCapabilities
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class CapabilitiesCallback(
    private val connectivityManager: ConnectivityManager,
    private val wifiManager: WifiManager,
    private val wifiRequest: NetworkRequest
) {

    private val _wifiCapabilities = callbackFlow {
        val capabilitiesCallback = object : NetworkCallback() {

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(
                    network,
                    networkCapabilities
                )

                val capabilities =
                    updateWifiCapabilities(networkCapabilities)

                trySendBlocking(capabilities)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }

            override fun onLost(network: Network) {
                super.onLost(network)

                val networkCapabilities = connectivityManager
                    .getNetworkCapabilities(network)

                val capabilities = if (networkCapabilities != null) {
                    updateWifiCapabilities(networkCapabilities)
                } else {
                    WifiCapabilities()
                }

                trySendBlocking(capabilities)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }
        }

        connectivityManager
            .registerNetworkCallback(
                wifiRequest,
                capabilitiesCallback
            )

        awaitClose {
            connectivityManager
                .unregisterNetworkCallback(
                    capabilitiesCallback
                )
        }
    }.buffer(Channel.CONFLATED)

    val wifiCapabilities: Flow<WifiCapabilities>
        get() = _wifiCapabilities

    private fun updateWifiCapabilities(
        networkCapabilities: NetworkCapabilities
    ): WifiCapabilities {
        val isAndroidQ = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q
        val isAndroidR = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.R
        val isAndroidS = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S

        val wifiInfo = if (isAndroidQ) {
            networkCapabilities
                .transportInfo as WifiInfo
        } else {
            wifiManager.connectionInfo
        }

        val capabilities = WifiCapabilities(
            wifiStandard = if (isAndroidR)
                wifiInfo.wifiStandard
            else -1,

            securityType = if (isAndroidS)
                wifiInfo.currentSecurityType
            else -1,

            frequency = wifiInfo.frequency,

            is24GHzSupported = if (isAndroidS)
                wifiManager.is24GHzBandSupported
            else false,
            is5GHzSupported = wifiManager.is5GHzBandSupported,
            is6GHzSupported = if (isAndroidR)
                wifiManager.is6GHzBandSupported
            else false,
            is60GHzSupported = if (isAndroidS)
                wifiManager.is60GHzBandSupported
            else false,

            downstreamBandwidthKbps = networkCapabilities
                .linkDownstreamBandwidthKbps,
            upstreamBandwidthKbps = networkCapabilities
                .linkUpstreamBandwidthKbps,

            frequencyUnits = WifiInfo.FREQUENCY_UNITS
        )

        return capabilities
    }
}