package com.humolang.wifiless.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.ui.viewmodels.MappingViewModel

@Composable
fun MappingScreen(
    mappingViewModel: MappingViewModel =
        viewModel(factory = MappingViewModel.Factory)
) {
    Text(text = "mapping screen")

    val mappingUiState by mappingViewModel
        .mappingUiState.collectAsStateWithLifecycle()
}