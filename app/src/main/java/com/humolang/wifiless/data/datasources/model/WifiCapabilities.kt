/*
 * Copyright (c) 2023  humolang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.humolang.wifiless.data.datasources.model

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import com.humolang.wifiless.R

data class WifiCapabilities(
    val isWifiEnabled: Boolean = false,

    val wifiStandard: Int = -1,
    val securityType: Int  = -1,

    val frequency: Int  = 0,
    val is24GHzSupported: Boolean = false,
    val is5GHzSupported: Boolean = false,
    val is6GHzSupported: Boolean = false,
    val is60GHzSupported: Boolean = false,

    val downstreamBandwidthKbps: Int = 0,
    val upstreamBandwidthKbps: Int = 0,

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

                else -> R.string.unknown
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

                else -> R.string.unknown
            }

            return id
        }
}
