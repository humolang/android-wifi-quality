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
                val isAndroidTiramisu = Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU

                val wifiInfo = if (isAndroidQ) {
                    networkCapabilities
                        .transportInfo as WifiInfo
                } else {
                    wifiManager.connectionInfo
                }

                val capabilities = WifiCapabilities(
                    downstreamBandwidthKbps = networkCapabilities
                        .linkDownstreamBandwidthKbps,
                    upstreamBandwidthKbps = networkCapabilities
                        .linkUpstreamBandwidthKbps,
                    signalStrength = if (isAndroidQ)
                        networkCapabilities.signalStrength
                    else -127,

                    bssid = wifiInfo.bssid,
                    securityType = if (isAndroidS)
                        wifiInfo.currentSecurityType
                    else -1,

                    frequency = wifiInfo.frequency,
                    hasHiddenSsid = wifiInfo.hiddenSSID,
                    macAddress = wifiInfo.macAddress,
                    maxSupportedRxLinkSpeedMbps = if (isAndroidR)
                        wifiInfo.maxSupportedRxLinkSpeedMbps
                    else -1,

                    maxSupportedTxLinkSpeedMbps = if (isAndroidR)
                        wifiInfo.maxSupportedTxLinkSpeedMbps
                    else -1,

                    fullyQualifiedDomainName = if (isAndroidQ)
                        wifiInfo.passpointFqdn ?: "-1"
                    else "-1",

                    providerFriendlyName = if (isAndroidQ)
                        wifiInfo.passpointProviderFriendlyName ?: "-1"
                    else "-1",

                    rssi = wifiInfo.rssi,
                    rxLinkSpeedMbps = if (isAndroidQ)
                        wifiInfo.rxLinkSpeedMbps
                    else -1,

                    ssid = wifiInfo.ssid,
                    txLinkSpeedMbps = if (isAndroidQ)
                        wifiInfo.txLinkSpeedMbps
                    else -1,

                    wifiStandard = if (isAndroidR)
                        wifiInfo.wifiStandard
                    else -1,

                    isRestricted = if (isAndroidTiramisu)
                        wifiInfo.isRestricted
                    else false,

                    frequencyUnits = WifiInfo.FREQUENCY_UNITS,
                    linkSpeedUnits = WifiInfo.LINK_SPEED_UNITS
                )

                trySendBlocking(capabilities)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }

//            override fun onLinkPropertiesChanged(
//                network: Network,
//                linkProperties: LinkProperties
//            ) {
//                super.onLinkPropertiesChanged(
//                    network,
//                    linkProperties
//                )
//            }
//
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