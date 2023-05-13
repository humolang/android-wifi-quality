package com.humolang.wifiless.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.repositories.PlanningTool
import com.humolang.wifiless.ui.states.PlanningUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlanningViewModel(
    private val planningTool: PlanningTool
) : ViewModel() {

    private val _planningUiState =
        MutableStateFlow(PlanningUiState())
    val planningUiState: StateFlow<PlanningUiState>
        get() = _planningUiState.asStateFlow()

    fun saveParameters(lengthInput: String, widthInput: String) {
        val length = lengthInput.toIntOrNull() ?: 1
        val width = widthInput.toIntOrNull() ?: 1

        planningTool.saveParameters(length, width)

        _planningUiState.value = _planningUiState.value.copy(
            parametersEntered = true,
            columns = planningTool.length,
            rows = planningTool.width,
            blocks = planningTool.blocks
        )
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