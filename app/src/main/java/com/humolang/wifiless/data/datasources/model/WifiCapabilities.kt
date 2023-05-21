package com.humolang.wifiless.data.datasources.model

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.UNKNOWN

data class WifiCapabilities(
    val wifiStandard: Int = -1,
    val securityType: Int  = -1,
    val frequency: Int  = 0,
    val downstreamBandwidthKbps: Int = 0,
    val upstreamBandwidthKbps: Int = 0,
    val hasHiddenSsid: Boolean = false,
    val ssid: String = UNKNOWN,
    val bssid: String = UNKNOWN,

    val frequencyUnits: String = ""
) {

    val securityTypeStringId: Int
        get() {
            val id = when (securityType) {
                WifiInfo.SECURITY_TYPE_OPEN ->
                    R.string.security_type_open

                WifiInfo.SECURITY_TYPE_WEP ->
                    R.string.security_type_wep

                WifiInfo.SECURITY_TYPE_PSK ->
                    R.string.security_type_psk

                WifiInfo.SECURITY_TYPE_EAP ->
                    R.string.security_type_eap

                WifiInfo.SECURITY_TYPE_SAE ->
                    R.string.security_type_sae

                WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE_192_BIT ->
                    R.string.security_type_wpa3_enterprise_192_bit

                WifiInfo.SECURITY_TYPE_OWE ->
                    R.string.security_type_owe

                WifiInfo.SECURITY_TYPE_WAPI_PSK ->
                    R.string.security_type_wapi_psk

                WifiInfo.SECURITY_TYPE_WAPI_CERT ->
                    R.string.security_type_wapi_cert

                WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE ->
                    R.string.security_type_wpa3_enterprise

                WifiInfo.SECURITY_TYPE_OSEN ->
                    R.string.security_type_osen

                WifiInfo.SECURITY_TYPE_PASSPOINT_R1_R2 ->
                    R.string.security_type_passpoint_r1_r2

                WifiInfo.SECURITY_TYPE_PASSPOINT_R3 ->
                    R.string.security_type_passpoint_r3

                WifiInfo.SECURITY_TYPE_DPP ->
                    R.string.security_type_dpp

                else -> R.string.security_type_unknown
            }

            return id
        }

    val wifiStandardStringId: Int
        get() {
            val id = when (wifiStandard) {
                ScanResult.WIFI_STANDARD_LEGACY ->
                    R.string.wifi_standard_legacy

                ScanResult.WIFI_STANDARD_11N ->
                    R.string.wifi_standard_11n

                ScanResult.WIFI_STANDARD_11AC ->
                    R.string.wifi_standard_11ac

                ScanResult.WIFI_STANDARD_11AX ->
                    R.string.wifi_standard_11ax

                ScanResult.WIFI_STANDARD_11AD ->
                    R.string.wifi_standard_11ad

                ScanResult.WIFI_STANDARD_11BE ->
                    R.string.wifi_standard_11be

                else -> R.string.wifi_standard_unknown
            }

            return id
        }
}
