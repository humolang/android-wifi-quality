package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.ui.viewmodels.HeatsViewModel

@Composable
fun HeatsScreen(
    navigateToMapping: (Int) -> Unit,
    heatsViewModel: HeatsViewModel =
        viewModel(factory = HeatsViewModel.Factory)
) {
    val heatsUiState by heatsViewModel
        .heatsUiState.collectAsStateWithLifecycle()

    val lazyState = rememberLazyListState()
    LazyColumn(
        state = lazyState,
        verticalArrangement = Arrangement
            .spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(heatsUiState.heats) { heat ->
            HeatItem(
                heat = heat,
                onHeatClicked = navigateToMapping
            )
        }
    }
}

@Composable
private fun HeatItem(
    heat: Heat,
    onHeatClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .border(
            2.dp,
            MaterialTheme.colorScheme.onBackground,
            RoundedCornerShape(4.dp)
        )
        .clickable {
            onHeatClicked(heat.id)
        }
    ) {
        Text(
            text = heat.name,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}