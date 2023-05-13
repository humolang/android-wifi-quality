package com.humolang.wifiless.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.ui.viewmodels.PlanningViewModel

@Composable
fun PlanningScreen(
    planningViewModel: PlanningViewModel =
        viewModel(factory = PlanningViewModel.Factory)
) {
    Text(text = "planning screen")

    val planningUiState by planningViewModel
        .planningUiState.collectAsStateWithLifecycle()
}