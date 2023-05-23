package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.repositories.HeatsRepository
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