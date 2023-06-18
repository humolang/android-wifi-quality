package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.datasources.model.WifiCapabilities
import com.humolang.wifiless.data.datasources.model.WifiProperties
import com.humolang.wifiless.data.repositories.WifiParameters
import com.humolang.wifiless.ui.states.LinkSpeedGraphState
import com.humolang.wifiless.ui.states.RssiGraphState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StartViewModel(
    private val wifiParameters: WifiParameters
) : ViewModel() {

    val dequeCapacity: Int
        get() = wifiParameters.dequeCapacity

    private val _rssiGraphState = MutableStateFlow(
        RssiGraphState(
            minRssi = wifiParameters.minRssi,
            rssiHorizontalCapacity = wifiParameters
                .rssiHorizontalCapacity
        )
    )
    val rssiGraphState: StateFlow<RssiGraphState>
        get() = _rssiGraphState.asStateFlow()

    private val _latestRssi =
        MutableStateFlow(wifiParameters.minRssi)
    val latestRssi: StateFlow<Int>
        get() = _latestRssi.asStateFlow()

    private val _rssiValues =
        MutableStateFlow(ArrayDeque<Int>())
    val rssiValues: StateFlow<ArrayDeque<Int>>
        get() = _rssiValues.asStateFlow()

    private val _linkSpeedGraphState = MutableStateFlow(
        LinkSpeedGraphState(
            maxLinkSpeed = wifiParameters.maxLinkSpeed,
            linkSpeedHorizontalCapacity = wifiParameters
                .linkSpeedHorizontalCapacity
        )
    )
    val linkSpeedGraphState: StateFlow<LinkSpeedGraphState>
        get() = _linkSpeedGraphState.asStateFlow()

    private val _latestLinkSpeed =
        MutableStateFlow(0)
    val latestLinkSpeed: StateFlow<Int>
        get() = _latestLinkSpeed.asStateFlow()

    val linkSpeedUnits: String
        get() = wifiParameters.linkSpeedUnits

    private val _linkSpeedValues =
        MutableStateFlow(ArrayDeque<Int>())
    val linkSpeedValues: StateFlow<ArrayDeque<Int>>
        get() = _linkSpeedValues.asStateFlow()

    private val _wifiCapabilities =
        MutableStateFlow(WifiCapabilities())
    val wifiCapabilities: StateFlow<WifiCapabilities>
        get() = _wifiCapabilities.asStateFlow()

    private val _wifiProperties =
        MutableStateFlow(WifiProperties())
    val wifiProperties: StateFlow<WifiProperties>
        get() = _wifiProperties.asStateFlow()

    private val _isWifiEnabled =
        MutableStateFlow(false)
    val isWifiEnabled: StateFlow<Boolean>
        get() = _isWifiEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            launch { collectLatestRssi() }
            launch { collectRssiValues() }
            launch { collectLatestLinkSpeed() }
            launch { collectLinkSpeedValues() }
            launch { collectWifiCapabilities() }
            launch { collectWifiProperties() }
        }
    }

    private suspend fun collectLatestRssi() {
        wifiParameters.latestRssi.collect { rssi ->
            if (isWifiEnabled.value) {
                if (wifiParameters.minRssi > rssi) {
                    wifiParameters.updateMinRssi(rssi)

                    _rssiGraphState.value = _rssiGraphState.value.copy(
                        minRssi = wifiParameters.minRssi
                    )
                }

                _latestRssi.value = rssi
            }
        }
    }

    private suspend fun collectRssiValues() {
        wifiParameters.rssiValues.collect { rssiDeque ->
            if (isWifiEnabled.value) {
                _rssiValues.value = rssiDeque
            }
        }
    }

    private suspend fun collectLatestLinkSpeed() {
        wifiParameters.latestLinkSpeed.collect { linkSpeed ->
            if (isWifiEnabled.value) {
                if (wifiParameters.maxLinkSpeed < linkSpeed) {
                    wifiParameters.updateMaxLinkSpeed(linkSpeed)

                    _linkSpeedGraphState.value = _linkSpeedGraphState.value.copy(
                        maxLinkSpeed = wifiParameters.maxLinkSpeed
                    )
                }

                _latestLinkSpeed.value = linkSpeed
            }
        }
    }

    private suspend fun collectLinkSpeedValues() {
        wifiParameters.linkSpeedValues.collect { linkSpeedDeque ->
            if (isWifiEnabled.value) {
                _linkSpeedValues.value = linkSpeedDeque
            }
        }
    }

    private suspend fun collectWifiCapabilities() {
        wifiParameters.wifiCapabilities.collect { capabilities ->
            _wifiCapabilities.value = capabilities

            _isWifiEnabled.value = capabilities
                .isWifiEnabled
        }
    }

    private suspend fun collectWifiProperties() {
        wifiParameters.wifiProperties.collect { properties ->
            _wifiProperties.value = properties
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val wifiParameters = (this[APPLICATION_KEY]
                        as WiFilessApplication).wifiParameters

                StartViewModel(
                    wifiParameters = wifiParameters
                )
            }
        }
    }
}