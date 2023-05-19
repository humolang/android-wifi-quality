package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.repositories.WifiParameters
import com.humolang.wifiless.ui.states.StartUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StartViewModel(
    private val wifiParameters: WifiParameters
) : ViewModel() {

    private val _startUiState = MutableStateFlow(
        StartUiState(
            dequeCapacity = wifiParameters.dequeCapacity,
            rssiHorizontalCapacity = wifiParameters
                .rssiHorizontalCapacity,
            minRssi = wifiParameters.minRssi,
            linkSpeedHorizontalCapacity = wifiParameters
                .linkSpeedHorizontalCapacity,
            maxLinkSpeed = wifiParameters.maxLinkSpeed
        )
    )
    val startUiState: StateFlow<StartUiState>
        get() = _startUiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { collectIsWifiConnected() }
            launch { collectLatestRssi() }
            launch { collectRssiValues() }
            launch { collectLatestSpeed() }
            launch { collectSpeedValues() }
            launch { collectIpAddress() }
        }
    }

    private suspend fun collectIsWifiConnected() {
        wifiParameters.isWifiConnected.collect { isWifiConnected ->
            _startUiState.value = _startUiState.value.copy(
                isWifiConnected = isWifiConnected
            )
        }
    }

    private suspend fun collectLatestRssi() {
        wifiParameters.latestRssi.collect { latestRssi ->
            if (wifiParameters.minRssi > latestRssi) {
                wifiParameters.updateMinRssi(latestRssi)

                _startUiState.value = _startUiState.value.copy(
                    latestRssi = latestRssi,
                    minRssi = wifiParameters.minRssi
                )
            } else {
                _startUiState.value = _startUiState.value.copy(
                    latestRssi = latestRssi
                )
            }
        }
    }

    private suspend fun collectRssiValues() {
        wifiParameters.rssiValues.collect { rssiValues ->
            _startUiState.value = _startUiState.value.copy(
                rssiValues = rssiValues
            )
        }
    }

    private suspend fun collectLatestSpeed() {
        wifiParameters.latestSpeed.collect { latestSpeed ->
            if (wifiParameters.maxLinkSpeed < latestSpeed) {
                wifiParameters.updateMaxLinkSpeed(latestSpeed)

                _startUiState.value = _startUiState.value.copy(
                    latestSpeed = latestSpeed,
                    maxLinkSpeed = wifiParameters.maxLinkSpeed
                )
            } else {
                _startUiState.value = _startUiState.value.copy(
                    latestSpeed = latestSpeed
                )
            }
        }
    }

    private suspend fun collectSpeedValues() {
        wifiParameters.speedValues.collect { speedValues ->
            _startUiState.value = _startUiState.value.copy(
                speedValues = speedValues
            )
        }
    }

    private suspend fun collectIpAddress() {
        wifiParameters.ipAddress.collect { ipAddress ->
            _startUiState.value = _startUiState.value.copy(
                ipAddress = ipAddress
            )
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