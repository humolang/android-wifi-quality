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

package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.repositories.MappingTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MappingViewModel(
    private val mappingTool: MappingTool
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

    val minRssi: Int
        get() = mappingTool.minRssi

    private suspend fun collectHeat() {
        mappingTool.heat.collect { heat ->
            _heat.value = heat
        }
    }

    private suspend fun collectBlocks() {
        mappingTool.blocks.collect { blocks ->
            _blocks.value = blocks
        }
    }

    fun loadHeatmap(heatId: Long) {
        viewModelScope.launch {
            mappingTool.loadHeat(heatId)
            mappingTool.loadBlocks(heatId)

            launch { collectHeat() }
            launch { collectBlocks() }
        }
    }

    fun checkRssi(heat: Heat, block: Block) {
        viewModelScope.launch {
            mappingTool.checkRssi(heat, block)
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