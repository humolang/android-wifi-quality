package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.repositories.HeatsRepository
import com.humolang.wifiless.ui.states.HeatsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HeatsViewModel(
    private val heatsRepository: HeatsRepository
) : ViewModel() {

    private val _heatsUiState =
        MutableStateFlow(HeatsUiState(
            heats = emptyList()
        ))
    val heatsUiState: StateFlow<HeatsUiState>
        get() = _heatsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            collectHeats()
        }
    }

    private suspend fun collectHeats() {
        heatsRepository.heats.collect { heats ->
            _heatsUiState.value = _heatsUiState.value.copy(
                heats = heats
            )
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