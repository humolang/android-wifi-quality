package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.repositories.MappingTool
import com.humolang.wifiless.ui.states.MappingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MappingViewModel(
    private val mappingTool: MappingTool
) : ViewModel() {

    private val _mappingUiState =
        MutableStateFlow(MappingUiState(
            blocks = emptyMap()
        ))
    val mappingUiState: StateFlow<MappingUiState>
        get() = _mappingUiState.asStateFlow()

    fun loadBlocks(heatId: Int) {
        viewModelScope.launch {
            val heat = mappingTool.loadHeatById(heatId)

            _mappingUiState.value = _mappingUiState.value.copy(
                heat = heat
            )

            mappingTool.loadBlocks(heat.id)
            launch { collectBlocks()  }
        }
    }

    private suspend fun collectBlocks() {
        mappingTool.blocks.collect { blocks ->
            _mappingUiState.value = _mappingUiState.value.copy(
                blocks = blocks
            )
        }
    }

    fun checkRssi(block: Block) {
        viewModelScope.launch {
            mappingTool.checkRssi(block)
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