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
            if (mappingTool.hasAccelerometer
                && mappingTool.hasMagnetic
                && mappingTool.hasGyroscope) {

                launch { collectPoints() }
            }
        }
    }

    private suspend fun collectPoints() {
        mappingTool.points.collect { points ->
            _mappingUiState.value = _mappingUiState.value.copy(
                points = points
            )
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