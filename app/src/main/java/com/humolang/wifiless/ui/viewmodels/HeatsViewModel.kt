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
import com.humolang.wifiless.data.repositories.HeatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HeatsViewModel(
    private val heatsRepository: HeatsRepository
) : ViewModel() {

    private val _heats = MutableStateFlow(
        emptyList<Heat>()
    )
    val heats: StateFlow<List<Heat>>
        get() = _heats.asStateFlow()

    init {
        viewModelScope.launch {
            collectHeats()
        }
    }

    private suspend fun collectHeats() {
        heatsRepository.heats.collect { heats ->
            _heats.value = heats
        }
    }

    fun deleteHeat(heat: Heat) {
        viewModelScope.launch {
            heatsRepository.deleteHeat(heat)
        }
    }

    fun loadBlocks(
        heatId: Long
    ): Flow<Map<Column, List<Block>>> {

        return heatsRepository
            .loadBlocks(heatId)
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val heatsRepository = (this[APPLICATION_KEY]
                        as WiFilessApplication).heatsRepository

                HeatsViewModel(
                    heatsRepository = heatsRepository
                )
            }
        }
    }
}