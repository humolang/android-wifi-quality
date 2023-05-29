package com.humolang.wifiless.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.datasources.model.BlockType
import com.humolang.wifiless.ui.screens.components.TransformableHeatmap
import com.humolang.wifiless.ui.viewmodels.PlanningViewModel
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningScreen(
    heatId: Long,
    popBackStack: () -> Unit,
    navigateToMapping: (Long) -> Unit,
    planningViewModel: PlanningViewModel =
        viewModel(factory = PlanningViewModel.Factory)
) {
    var id by rememberSaveable {
        mutableStateOf(heatId)
    }

    LaunchedEffect(key1 = heatId) {
        id = planningViewModel.loadHeatmap(id)
    }

    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior()
    
    Scaffold(
        modifier = Modifier
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),
        topBar = {
            PlanningTopBar(
                heatFlow = planningViewModel.heat,
                onNameAcceptedClicked = { heat, name ->
                    planningViewModel.updateHeatName(heat, name)
                },
                popBackStack = popBackStack,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            val heat by planningViewModel.heat
                .collectAsStateWithLifecycle()

            PlanningBottomBar(
                onRowTopClicked = {
                    planningViewModel
                        .insertRow(heat.id, 0)
                },
                onRowBottomClicked = {
                    planningViewModel
                        .insertRow(heat.id, heat.rows)
                },
                onColumnRightClicked = {
                    planningViewModel
                        .insertColumn(heat.id, heat.columns)
                },
                onColumnLeftClicked = {
                    planningViewModel
                        .insertColumn(heat.id, 0)
                },
                navigateToMapping = {
                    navigateToMapping(heat.id)
                }
            )
        },
        content = { innerPadding ->
            PlanningContent(
                planningViewModel = planningViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanningTopBar(
    heatFlow: StateFlow<Heat>,
    onNameAcceptedClicked: (Heat, String) -> Unit,
    popBackStack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val heat by heatFlow
        .collectAsStateWithLifecycle()

    var heatName by remember {
        mutableStateOf(heat.name)
    }
    var edited by remember {
        mutableStateOf(true)
    }

    val nameFieldRequester = remember {
        FocusRequester()
    }

    TopAppBar(
        modifier = modifier,
        title = {
            if (edited) {
                Text(
                    text = heat.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                TextField(
                    value = heatName,
                    onValueChange = { heatName = it},
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.plan_name
                            )
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .focusRequester(nameFieldRequester)
                )

                LaunchedEffect(
                    key1 = nameFieldRequester
                ) {
                    nameFieldRequester.requestFocus()
                }
            }
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
            if (edited) {
                IconButton(
                    onClick = {
                        edited = !edited
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Edit,
                        contentDescription = stringResource(
                            id = R.string.edit_name
                        )
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        edited = !edited
                        onNameAcceptedClicked(heat, heatName)
                    }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Done,
                        contentDescription = stringResource(
                            id = R.string.accept_name
                        )
                    )
                }
            }

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
        colors = if (edited)
            TopAppBarDefaults.topAppBarColors()
        else TopAppBarDefaults
            .topAppBarColors(
                containerColor = MaterialTheme.colorScheme
                    .surfaceVariant
            ),

        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun PlanningBottomBar(
    onRowTopClicked: () -> Unit,
    onRowBottomClicked: () -> Unit,
    onColumnRightClicked: () -> Unit,
    onColumnLeftClicked: () -> Unit,
    navigateToMapping: () -> Unit
) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = onRowTopClicked
            ) {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_keyboard_double_arrow_up_24
                    ),
                    contentDescription = stringResource(
                        id = R.string.insert_row_top
                    )
                )
            }
            IconButton(
                onClick = onColumnRightClicked
            ) {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_keyboard_double_arrow_right_24
                    ),
                    contentDescription = stringResource(
                        id = R.string.insert_column_right
                    )
                )
            }
            IconButton(
                onClick = onRowBottomClicked
            ) {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_keyboard_double_arrow_down_24
                    ),
                    contentDescription = stringResource(
                        id = R.string.insert_row_bottom
                    )
                )
            }
            IconButton(
                onClick = onColumnLeftClicked
            ) {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_keyboard_double_arrow_left_24
                    ),
                    contentDescription = stringResource(
                        id = R.string.insert_column_left
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToMapping,
                containerColor = BottomAppBarDefaults
                    .bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults
                    .bottomAppBarFabElevation()
            ) {
                Icon(
                    Icons.TwoTone.Done,
                    contentDescription = stringResource(
                        id = R.string.done
                    )
                )
            }
        }
    )
}

@Composable
private fun PlanningContent(
    planningViewModel: PlanningViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PlanningField(
            heatFlow = planningViewModel.heat,
            blocksFlow = planningViewModel.blocks,
            onBlockTypeClicked = { block, type ->
                planningViewModel
                    .updateBlockType(block, type)
            },
            onInsertRowClicked = { heatId, y ->
                planningViewModel
                    .insertRow(heatId, y)
            },
            onInsertColumnClicked = { heatId, x ->
                planningViewModel
                    .insertColumn(heatId, x)
            },
            onDeleteRowClicked = { heatId, y ->
                planningViewModel
                    .deleteRow(heatId, y)
            },
            onDeleteColumnClicked = { heatId, x ->
                planningViewModel
                    .deleteColumn(heatId, x)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PlanningField(
    heatFlow: StateFlow<Heat>,
    blocksFlow: StateFlow<Map<Column, List<Block>>>,
    onBlockTypeClicked: (Block, BlockType) -> Unit,
    onInsertRowClicked: (Long, Int) -> Unit,
    onInsertColumnClicked: (Long, Int) -> Unit,
    onDeleteRowClicked: (Long, Int) -> Unit,
    onDeleteColumnClicked: (Long, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val heat by heatFlow
            .collectAsStateWithLifecycle()
        val blocks by blocksFlow
            .collectAsStateWithLifecycle()

        var typeMenuExpanded by remember {
            mutableStateOf(false)
        }
        var editMenuExpanded by remember {
            mutableStateOf(false)
        }

        var selectedColumn by remember {
            mutableStateOf(
                Column(heatId = 0L, x = 0)
            )
        }

        var selectedBlock by remember {
            mutableStateOf(
                Block(
                    columnId = 0L,
                    y = 0
                )
            )
        }

        TransformableHeatmap(
            heat = heat,
            blocks = blocks,
            onBlockClicked = { block ->
                selectedBlock = block
                typeMenuExpanded = true
            },
            onBlockLongClicked = { column, block ->
                selectedColumn = column
                selectedBlock = block

                editMenuExpanded = true
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

        BlockTypeMenu(
            expanded = typeMenuExpanded,
            onDismissRequest = { typeMenuExpanded = false },
            block = selectedBlock,
            onBlockTypeClicked = onBlockTypeClicked
        )

        EditPlanMenu(
            expanded = editMenuExpanded,
            onDismissRequest = { editMenuExpanded = false },
            heat = heat,
            column = selectedColumn,
            block = selectedBlock,
            onInsertRowClicked = onInsertRowClicked,
            onInsertColumnClicked = onInsertColumnClicked,
            onDeleteRowClicked = onDeleteRowClicked,
            onDeleteColumnClicked = onDeleteColumnClicked
        )
    }
}

@Composable
private fun BlockTypeMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    block: Block,
    onBlockTypeClicked: (Block, BlockType) -> Unit,
    modifier: Modifier = Modifier
) {
    val onItemClicked = { type: BlockType ->
        onBlockTypeClicked(block, type)
        onDismissRequest()
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            stringId = R.string.armchair,
            drawableId = R.drawable.twotone_chair_24
        ) {
            onItemClicked(BlockType.ARMCHAIR)
        }

        DropdownMenuItem(
            stringId = R.string.chair,
            drawableId = R.drawable.twotone_chair_alt_24
        ) {
            onItemClicked(BlockType.CHAIR)
        }

        DropdownMenuItem(
            stringId = R.string.computer,
            drawableId = R.drawable.twotone_computer_24
        ) {
            onItemClicked(BlockType.COMPUTER)
        }

        DropdownMenuItem(
            stringId = R.string.router,
            drawableId = R.drawable.twotone_router_24
        ) {
            onItemClicked(BlockType.ROUTER)
        }

        DropdownMenuItem(
            stringId = R.string.table,
            drawableId = R.drawable.twotone_table_restaurant_24
        ) {
            onItemClicked(BlockType.TABLE)
        }

        DropdownMenuItem(
            stringId = R.string.tv,
            drawableId = R.drawable.twotone_tv_24
        ) {
            onItemClicked(BlockType.TV)
        }

        DropdownMenuItem(
            stringId = R.string.window,
            drawableId = R.drawable.twotone_window_24
        ) {
            onItemClicked(BlockType.WINDOW)
        }

        DropdownMenuItem(
            stringId = R.string.wall,
            drawableId = R.drawable.twotone_fence_24
        ) {
            onItemClicked(BlockType.WALL)
        }

        DropdownMenuItem(
            stringId = R.string.free,
            drawableId = R.drawable.twotone_air_24
        ) {
            onItemClicked(BlockType.FREE)
        }
    }
}

@Composable
private fun EditPlanMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    heat: Heat,
    column: Column,
    block: Block,
    onInsertRowClicked: (Long, Int) -> Unit,
    onInsertColumnClicked: (Long, Int) -> Unit,
    onDeleteRowClicked: (Long, Int) -> Unit,
    onDeleteColumnClicked: (Long, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            stringId = R.string.insert_row_top,
            drawableId = R.drawable
                .twotone_keyboard_double_arrow_up_24
        ) {
            onInsertRowClicked(heat.id, block.y)
            onDismissRequest()
        }
        DropdownMenuItem(
            stringId = R.string.insert_column_right,
            drawableId = R.drawable
                .twotone_keyboard_double_arrow_right_24
        ) {
            onInsertColumnClicked(heat.id, column.x + 1)
            onDismissRequest()
        }
        DropdownMenuItem(
            stringId = R.string.insert_row_bottom,
            drawableId = R.drawable
                .twotone_keyboard_double_arrow_down_24
        ) {
            onInsertRowClicked(heat.id, block.y + 1)
            onDismissRequest()
        }
        DropdownMenuItem(
            stringId = R.string.insert_column_left,
            drawableId = R.drawable
                .twotone_keyboard_double_arrow_left_24
        ) {
            onInsertColumnClicked(heat.id, column.x)
            onDismissRequest()
        }

        Divider()

        DropdownMenuItem(
            stringId = R.string.delete_row,
            imageVector = Icons.TwoTone.Delete
        ) {
            onDeleteRowClicked(heat.id, block.y)
            onDismissRequest()
        }
        DropdownMenuItem(
            stringId = R.string.delete_column,
            imageVector = Icons.TwoTone.Delete
        ) {
            onDeleteColumnClicked(heat.id, column.x)
            onDismissRequest()
        }
    }
}

@Composable
private fun DropdownMenuItem(
    @StringRes stringId: Int,
    @DrawableRes drawableId: Int,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(
                    id = stringId
                )
            )
        },
        onClick = onClick,
        leadingIcon = {
            Icon(
                painter = painterResource(
                    id = drawableId
                ),
                contentDescription = null
            )
        }
    )
}

@Composable
private fun DropdownMenuItem(
    @StringRes stringId: Int,
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(
                    id = stringId
                )
            )
        },
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
    )
}