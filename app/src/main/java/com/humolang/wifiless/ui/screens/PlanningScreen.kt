package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Create
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
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
import com.humolang.wifiless.ui.viewmodels.PlanningViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningScreen(
    heatId: Long,
    popBackStack: () -> Unit,
    navigateToMapping: (Long) -> Unit,
    planningViewModel: PlanningViewModel =
        viewModel(factory = PlanningViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior()

    var created by remember {
        mutableStateOf(false)
    }
    
    Scaffold(
        modifier = Modifier
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),
        topBar = {
            PlanningTopBar(
                created = created,
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
                created = created,
                onRowTopClicked = {
                    planningViewModel
                        .insertTopRow(heat.id)
                },
                onRowBottomClicked = {
                    planningViewModel
                        .insertBottomRow(heat.id)
                },
                onColumnRightClicked = {
                    planningViewModel
                        .insertRightColumn(heat.id)
                },
                onColumnLeftClicked = {
                    planningViewModel
                        .insertLeftColumn(heat.id)
                },
                onCreateClicked = {
                    planningViewModel
                        .loadHeatmap(heatId)

                    created = !created
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
    created: Boolean,
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
                    if (created)
                        heat.name
                    else stringResource(id = R.string.room_plan),

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
            if (created) {
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
    created: Boolean,
    onRowTopClicked: () -> Unit,
    onRowBottomClicked: () -> Unit,
    onColumnRightClicked: () -> Unit,
    onColumnLeftClicked: () -> Unit,
    onCreateClicked: () -> Unit,
    navigateToMapping: () -> Unit
) {
    BottomAppBar(
        actions = {
            if (created) {
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
                            id = R.drawable.sharp_keyboard_double_arrow_left_24
                        ),
                        contentDescription = stringResource(
                            id = R.string.insert_column_left
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (!created) {
                FloatingActionButton(
                    onClick = {
                        onCreateClicked()
                    },
                    containerColor = BottomAppBarDefaults
                        .bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults
                        .bottomAppBarFabElevation()
                ) {
                    Icon(
                        Icons.TwoTone.Create,
                        contentDescription = stringResource(
                            id = R.string.create_plan
                        )
                    )
                }
            } else {
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
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PlanningField(
    heatFlow: StateFlow<Heat>,
    blocksFlow: StateFlow<Map<Column, List<Block>>>,
    onBlockTypeClicked: (Block, BlockType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        RoomPlan(
            heatFlow = heatFlow,
            blocksFlow = blocksFlow,
            onBlockTypeClicked = onBlockTypeClicked,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun RoomPlan(
    heatFlow: StateFlow<Heat>,
    blocksFlow: StateFlow<Map<Column, List<Block>>>,
    onBlockTypeClicked: (Block, BlockType) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    val heat by heatFlow
        .collectAsStateWithLifecycle()
    val blocks by blocksFlow
        .collectAsStateWithLifecycle()

    val ratioValue = heat.columns.toFloat() / heat.rows

    Row(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = state)
            .aspectRatio(ratioValue)
    ) {
        for (column in blocks) {

            Column(modifier = Modifier.weight(1f)) {
                for (block in column.value) {

                    Block(
                        block = block,
                        onBlockTypeClicked = onBlockTypeClicked,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Block(
    block: Block,
    onBlockTypeClicked: (Block, BlockType) -> Unit,
    modifier: Modifier = Modifier
) {
    val tertiaryBorder = MaterialTheme.colorScheme.onTertiaryContainer
    val tertiaryRectangle = MaterialTheme.colorScheme.tertiaryContainer

    val hasRssi = abs(block.rssi) in 0..100
    val rssiGreen = abs(block.rssi.toFloat()) / 100

    val borderColor = Color(
        tertiaryBorder.red,
        if (hasRssi) rssiGreen else tertiaryBorder.green,
        tertiaryBorder.blue,
        tertiaryBorder.alpha,
        tertiaryBorder.colorSpace
    )

    val rectangleColor = Color(
        tertiaryRectangle.red,
        if (hasRssi) rssiGreen else tertiaryRectangle.green,
        tertiaryRectangle.blue,
        tertiaryRectangle.alpha,
        tertiaryRectangle.colorSpace
    )

    var expanded by remember {
        mutableStateOf(false)
    }

    when (block.type) {

        BlockType.WALL -> {
            BlockDrawer(
                borderColor = borderColor,
                drawBlock = {
                    drawWallBlock(
                        rectangleColor = rectangleColor,
                        lineColor = borderColor,
                        cornerRadius = CornerRadius(
                            4.dp.toPx(),
                            4.dp.toPx()
                        ),
                        size = size
                    )
                },
                modifier = modifier
                    .clickable { expanded = true }
            )
        }

        BlockType.FREE -> {
            BlockDrawer(
                borderColor = borderColor,
                drawBlock = {
                    drawFreeBlock(
                        rectangleColor = rectangleColor,
                        cornerRadius = CornerRadius(
                            4.dp.toPx(),
                            4.dp.toPx()
                        )
                    )
                },
                modifier = modifier
                    .clickable { expanded = true }
            )
        }

        else -> {
            Icon(
                painter = painterResource(id = block.imageId),
                contentDescription = null,
                modifier = modifier
                    .border(
                        2.dp,
                        borderColor,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(2.dp)
                    .drawBehind {
                        drawFreeBlock(
                            rectangleColor = rectangleColor,
                            cornerRadius = CornerRadius(
                                4.dp.toPx(),
                                4.dp.toPx()
                            )
                        )
                    }
                    .clickable { expanded = true }
            )
        }
    }

    BlockTypeMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        block = block,
        onBlockTypeClicked = onBlockTypeClicked
    )
}

@Composable
private fun BlockDrawer(
    borderColor: Color,
    drawBlock: DrawScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .border(
                2.dp,
                borderColor,
                RoundedCornerShape(4.dp)
            )
            .padding(2.dp)
    ) {
        drawBlock()
    }
}

private fun DrawScope.drawFreeBlock(
    rectangleColor: Color,
    cornerRadius: CornerRadius
) {
    drawRoundRect(
        color = rectangleColor,
        cornerRadius = cornerRadius
    )
}

private fun DrawScope.drawWallBlock(
    rectangleColor: Color,
    lineColor: Color,
    cornerRadius: CornerRadius,
    size: Size
) {
    drawRoundRect(
        color = rectangleColor,
        cornerRadius = cornerRadius
    )

    val lineWidth = 2.dp.toPx()

    val times = 6

    val intervalX = size.width / times
    val intervalY = size.height / times

    repeat(times) { number ->
        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f,
                y = size.height - (intervalY * number)
            ),
            end = Offset(
                x = size.width - (intervalX * number),
                y = 0f
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f + (intervalX * number),
                y = size.height
            ),
            end = Offset(
                x = size.width,
                y = 0f + (intervalY * number)
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f + (intervalY * number),
                y = 0f
            ),
            end = Offset(
                x = size.width,
                y = size.height - (intervalX * number)
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f,
                y = 0f + (intervalY * number)
            ),
            end = Offset(
                x = size.width - (intervalX * number),
                y = size.height
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
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
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.armchair
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.ARMCHAIR
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_chair_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.chair
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.CHAIR
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_chair_alt_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.computer
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.COMPUTER
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_computer_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.door
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.DOOR
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_door_front_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.router
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.ROUTER
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_router_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.table
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.TABLE
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_table_restaurant_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.tv
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.TV
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_tv_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.window
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.WINDOW
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    painterResource(
                        id = R.drawable.twotone_window_24
                    ),
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.wall
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.WALL
                )
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.free
                    )
                )
            },
            onClick = {
                onBlockTypeClicked(
                    block,
                    BlockType.FREE
                )
                onDismissRequest()
            }
        )
    }
}