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

    private val _startUiState = MutableStateFlow(StartUiState())
    val startUiState: StateFlow<StartUiState>
        get() = _startUiState.asStateFlow()

    val dequeCapacity: Int
        get() = wifiParameters.dequeCapacity

    init {
        viewModelScope.launch {
            launch { collectIsWifiConnected() }
            launch { collectLatestRssi() }
            launch { collectRssiValues() }
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
            _startUiState.value = _startUiState.value.copy(
                latestRssi = latestRssi
            )
        }
    }

    private suspend fun collectRssiValues() {
        wifiParameters.rssiValues.collect { rssiValues ->
            _startUiState.value = _startUiState.value.copy(
                rssiValues = rssiValues
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