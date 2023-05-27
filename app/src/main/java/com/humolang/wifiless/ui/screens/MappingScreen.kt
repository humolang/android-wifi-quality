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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

@OptIn(ExperimentalMaterial3Api::class)
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

    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),
        topBar = {
            MappingTopBar(
                heatFlow = mappingViewModel.heat,
                popBackStack = popBackStack,
                scrollBehavior = scrollBehavior
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
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val heat by heatFlow
        .collectAsStateWithLifecycle()

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
        actions = {
//            IconButton(
//                onClick = { /* doSomething() */ }
//            ) {
//                Icon(
//                    imageVector = Icons.TwoTone.Delete,
//                    contentDescription = stringResource(
//                        id = R.string.delete
//                    )
//                )
//            }
        },
        scrollBehavior = scrollBehavior
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
            onBlockClicked = { block ->
                mappingViewModel.checkRssi(block)
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
    onBlockClicked: (Block) -> Unit,
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

        TransformableHeatmap(
            heat = heat,
            blocks = blocks,
            onBlockClicked = onBlockClicked,
            modifier = Modifier.padding(16.dp)
        )
    }
}