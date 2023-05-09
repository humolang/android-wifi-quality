package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.repositories.MappingTool
import com.humolang.wifiless.ui.states.MappingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MappingViewModel(
    private val mappingTool: MappingTool
) : ViewModel() {

    private val _mappingUiState = MutableStateFlow(
        MappingUiState(
            hasAccelerometer = mappingTool.hasAccelerometer,
            hasMagnetic = mappingTool.hasMagnetic
        )
    )
    val mappingUiState: StateFlow<MappingUiState>
        get() = _mappingUiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (mappingTool.hasAccelerometer) {
                launch { collectCalibrated() }
                launch { collectAcceleration() }
                launch { collectVelocity() }
                launch { collectDistance() }
            }
            if (mappingTool.hasMagnetic) {
                launch { collectMagnetic() }
            }
            if (mappingTool.hasAccelerometer
                && mappingTool.hasMagnetic) {

                launch { collectPoints() }
            }
        }
    }

    private suspend fun collectCalibrated() {
        mappingTool.calibrated.collect { calibrated ->
            _mappingUiState.value = _mappingUiState.value.copy(
                calibrated = calibrated
            )
        }
    }

    private suspend fun collectAcceleration() {
        mappingTool.acceleration.collect { acceleration ->
            _mappingUiState.value = _mappingUiState.value.copy(
                acceleration = acceleration
            )
        }
    }

    private suspend fun collectVelocity() {
        mappingTool.velocity.collect { velocity ->
            _mappingUiState.value = _mappingUiState.value.copy(
                velocity = velocity
            )
        }
    }

    private suspend fun collectDistance() {
        mappingTool.distance.collect { distance ->
            _mappingUiState.value = _mappingUiState.value.copy(
                distance = distance
            )
        }
    }

    private suspend fun collectMagnetic() {
        mappingTool.magnetic.collect { magnetic ->
            _mappingUiState.value = _mappingUiState.value.copy(
                magnetic = magnetic
            )
        }
    }

    private suspend fun collectPoints() {
        mappingTool.points.collect { points ->
            _mappingUiState.value = _mappingUiState.value.copy(
                points = points
            )
        }
    }

    fun calibrateAccelerometer() {
        if (mappingTool.hasAccelerometer) {
            viewModelScope.launch {
                mappingTool.calibrateAccelerometer()
            }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val mappingTool = (this[APPLICATION_KEY]
                        as WiFilessApplication).mappingTool

                MappingViewModel(
                    mappingTool = mappingTool
                )
            }
        }
    }
}