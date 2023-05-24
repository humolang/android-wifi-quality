package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.datasources.DEFAULT_HEAT_ID
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.datasources.model.BlockType
import com.humolang.wifiless.data.repositories.PlanningTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlanningViewModel(
    private val planningTool: PlanningTool
) : ViewModel() {

    private val _heat =
        MutableStateFlow(Heat())
    val heat: StateFlow<Heat>
        get() = _heat.asStateFlow()

    private val _blocks = MutableStateFlow(
        emptyMap<Column, List<Block>>()
    )
    val blocks: StateFlow<Map<Column, List<Block>>>
        get() = _blocks.asStateFlow()

    suspend fun loadHeatmap(heatId: Long): Long {
        val id = if (heatId == DEFAULT_HEAT_ID) {
            planningTool
                .initialHeatmap()
        } else {
            heatId
        }

        planningTool.loadHeat(id)
        planningTool.loadBlocks(id)

        viewModelScope.launch {
            launch { collectHeat() }
            launch { collectBlocks() }
        }

        return id
    }

    private suspend fun collectHeat() {
        planningTool.heat.collect { heat ->
            _heat.value = heat
        }
    }

    private suspend fun collectBlocks() {
        planningTool.blocks.collect { blocks ->
            _blocks.value = blocks
        }
    }

    fun updateBlockType(
        block: Block,
        type: BlockType
    ) {
        viewModelScope.launch {
            planningTool.updateBlockType(block, type)
        }
    }

    fun updateHeatName(
        heat: Heat,
        name: String
    ) {
        viewModelScope.launch {
            planningTool.updateHeatName(heat, name)
        }
    }

    fun insertTopRow(heatId: Long) {
        viewModelScope.launch {
            planningTool.insertTopRow(heatId)
        }
    }

    fun insertBottomRow(heatId: Long) {
        viewModelScope.launch {
            planningTool.insertBottomRow(heatId)
        }
    }

    fun insertRightColumn(heatId: Long) {
        viewModelScope.launch {
            planningTool.insertRightColumn(heatId)
        }
    }

    fun insertLeftColumn(heatId: Long) {
        viewModelScope.launch {
            planningTool.insertLeftColumn(heatId)
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