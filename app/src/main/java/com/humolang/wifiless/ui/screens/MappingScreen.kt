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

package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.ui.screens.components.RssiHorizontalScale
import com.humolang.wifiless.ui.screens.components.TransformableHeatmap
import com.humolang.wifiless.ui.viewmodels.MappingViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MappingScreen(
    heatId: Long,
    popBackStack: () -> Unit,
    navigateToStart: (Long) -> Unit,
    mappingViewModel: MappingViewModel =
        viewModel(factory = MappingViewModel.Factory)
) {
    LaunchedEffect(key1 = heatId) {
        mappingViewModel.loadHeatmap(heatId)
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            MappingTopBar(
                heatFlow = mappingViewModel.heat,
                popBackStack = popBackStack
            )
        },
        floatingActionButton = {
            val heat by mappingViewModel.heat
                .collectAsStateWithLifecycle()

            FloatingActionButton(
                onClick = {
                    navigateToStart(heat.id)
                },
            ) {
                Icon(
                    Icons.TwoTone.Done,
                    contentDescription = stringResource(
                        id = R.string.done
                    )
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            MappingContent(
                mappingViewModel = mappingViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MappingTopBar(
    heatFlow: StateFlow<Heat>,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heat by heatFlow
        .collectAsStateWithLifecycle()

    val appBarContainerColor = MaterialTheme
        .colorScheme
        .surfaceColorAtElevation(3.dp)

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = heat.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.back
                    )
                )
            }
        },
        actions = {  },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appBarContainerColor
        )
    )
}

@Composable
private fun MappingContent(
    mappingViewModel: MappingViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        RssiHorizontalScale(
            minRssi = mappingViewModel.minRssi,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(16.dp)
        )

        MappingField(
            heatFlow = mappingViewModel.heat,
            blocksFlow = mappingViewModel.blocks,
            onBlockClicked = { heat, block ->
                mappingViewModel.checkRssi(heat, block)
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
private fun MappingField(
    heatFlow: StateFlow<Heat>,
    blocksFlow: StateFlow<Map<Column, List<Block>>>,
    onBlockClicked: (Heat, Block) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        val heat by heatFlow
            .collectAsStateWithLifecycle()
        val blocks by blocksFlow
            .collectAsStateWithLifecycle()

        if (blocks.isNotEmpty()) {
            TransformableHeatmap(
                heat = heat,
                blocks = blocks,
                onBlockClicked = { block ->
                    onBlockClicked(heat, block)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}