package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.repositories.PlanningTool
import com.humolang.wifiless.ui.states.PlanningUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlanningViewModel(
    private val planningTool: PlanningTool
) : ViewModel() {

    private val _planningUiState =
        MutableStateFlow(PlanningUiState(
            blocks = emptyMap()
        ))
    val planningUiState: StateFlow<PlanningUiState>
        get() = _planningUiState.asStateFlow()

    fun initialHeat(
        name: String,
        length: String,
        width: String
    ) {
        viewModelScope.launch {
            val heatId = planningTool.insertHeat(name, length, width)
            val heat = planningTool.loadHeatById(heatId.toInt())

            _planningUiState.value = _planningUiState.value.copy(
                heat = heat
            )

            for (column in 0 until heat.length) {
                launch { initialColumn(heat, column) }
            }

            planningTool.loadBlocks(heat.id)
            launch { collectBlocks() }
        }
    }

    private suspend fun initialColumn(heat: Heat, column: Int) {
        val columnId = planningTool
            .insertColumn(heat.id, column)

        for (row in 0 until heat.width) {
            planningTool.insertBlock(columnId.toInt(), row)
        }
    }

    private suspend fun collectBlocks() {
        planningTool.blocks.collect { blocks ->
            _planningUiState.value = _planningUiState.value.copy(
                blocks = blocks
            )
        }
    }

    fun resetHeat() {
        viewModelScope.launch {
            val heat = _planningUiState.value.heat
            if (heat != null) {
                planningTool.deleteHeat(heat)

                _planningUiState.value = _planningUiState.value.copy(
                    heat = null
                )
            }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val planningTool = (this[APPLICATION_KEY]
                        as WiFilessApplication).planningTool

                PlanningViewModel(
                    planningTool = planningTool
                )
            }
        }
    }
}