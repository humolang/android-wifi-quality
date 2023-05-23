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

                    downstreamBandwidthKbps = networkCapabilities
                        .linkDownstreamBandwidthKbps,
                    upstreamBandwidthKbps = networkCapabilities
                        .linkUpstreamBandwidthKbps,

                    hasHiddenSsid = wifiInfo.hiddenSSID,
                    ssid = wifiInfo.ssid,
                    bssid = wifiInfo.bssid,

                    frequencyUnits = WifiInfo.FREQUENCY_UNITS
                )

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
}