package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.DEFAULT_HEAT_ID
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.ui.viewmodels.HeatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatsScreen(
    popBackStack: () -> Unit,
    navigateToPlanning: (Long) -> Unit,
    navigateToMapping: (Long) -> Unit,
    heatsViewModel: HeatsViewModel =
        viewModel(factory = HeatsViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),
        topBar = {
            HeatsTopBar(
                popBackStack = popBackStack,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateToPlanning(DEFAULT_HEAT_ID)
                },
                containerColor = BottomAppBarDefaults
                    .bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults
                    .bottomAppBarFabElevation()
            ) {
                Icon(
                    Icons.TwoTone.Add,
                    contentDescription = stringResource(
                        id = R.string.create_plan
                    )
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            HeatsContent(
                onHeatClicked = { heatId ->
                    navigateToMapping(heatId)
                },
                onEditClicked = { heatId ->
                    navigateToPlanning(heatId)
                },
                onDeleteClicked = { heat ->
                    heatsViewModel.deleteHeat(heat)
                },
                heatsViewModel = heatsViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeatsTopBar(
    popBackStack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(
                    id = R.string.heatmaps
                ),
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
//                    imageVector = Icons.TwoTone.MoreVert,
//                    contentDescription = stringResource(
//                        id = R.string.menu
//                    )
//                )
//            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun HeatsContent(
    onHeatClicked: (Long) -> Unit,
    onEditClicked: (Long) -> Unit,
    onDeleteClicked: (Heat) -> Unit,
    heatsViewModel: HeatsViewModel,
    modifier: Modifier = Modifier
) {
    val heats by heatsViewModel.heats
        .collectAsStateWithLifecycle()

    val lazyState = rememberLazyListState()

    LazyColumn(
        state = lazyState,
        verticalArrangement = Arrangement
            .spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(heats) { heat ->
            HeatItem(
                heat = heat,
                onHeatClicked = onHeatClicked,
                onEditClicked = onEditClicked,
                onDeleteClicked = onDeleteClicked
            )
        }
    }
}

@Composable
private fun HeatItem(
    heat: Heat,
    onHeatClicked: (Long) -> Unit,
    onEditClicked: (Long) -> Unit,
    onDeleteClicked: (Heat) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onHeatClicked(heat.id)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = heat.name,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    onEditClicked(heat.id)
                }
            ) {
                Icon(
                    Icons.TwoTone.Edit,
                    contentDescription = stringResource(
                        id = R.string.edit_plan
                    )
                )
            }

            IconButton(
                onClick = {
                    onDeleteClicked(heat)
                }
            ) {
                Icon(
                    Icons.TwoTone.Delete,
                    contentDescription = stringResource(
                        id = R.string.delete_plan
                    )
                )
            }
        }
    }
}