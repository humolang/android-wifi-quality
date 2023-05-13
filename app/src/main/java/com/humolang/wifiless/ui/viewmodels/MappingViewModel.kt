package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.repositories.MappingTool
import com.humolang.wifiless.ui.states.MappingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MappingViewModel(
    private val mappingTool: MappingTool
) : ViewModel() {

    private val _mappingUiState = MutableStateFlow(
        MappingUiState()
    )
    val mappingUiState: StateFlow<MappingUiState>
        get() = _mappingUiState.asStateFlow()

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