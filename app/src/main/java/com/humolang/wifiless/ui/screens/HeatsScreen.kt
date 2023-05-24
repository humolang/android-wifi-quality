package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.DEFAULT_HEAT_ID
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.datasources.model.BlockType
import com.humolang.wifiless.ui.viewmodels.HeatsViewModel
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

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
                }
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
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 128.dp
        ),
        verticalArrangement = Arrangement
            .spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(heats) { heat ->
            HeatItem(
                heat = heat,
                onHeatClicked = onHeatClicked,
                onEditClicked = onEditClicked,
                onDeleteClicked = onDeleteClicked,
                loadBlocks = { heatId ->
                    heatsViewModel.loadBlocks(heatId)
                }
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
    loadBlocks: (Long) -> Flow<Map<Column, List<Block>>>,
    modifier: Modifier = Modifier
) {

    var cardExpanded by remember {
        mutableStateOf(false)
    }
    var menuOpened by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onHeatClicked(heat.id)
            }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = heat.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(
                            id = R.string.heat_size,
                            heat.columns,
                            heat.rows
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = heat.modificationDate,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { menuOpened = true }
                ) {
                    Icon(
                        Icons.TwoTone.MoreVert,
                        contentDescription = stringResource(
                            id = R.string.heatmap_options
                        )
                    )
                }

                if (!cardExpanded) {
                    IconButton(
                        onClick = { cardExpanded = !cardExpanded }
                    ) {
                        Icon(
                            Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                } else {
                    IconButton(
                        onClick = { cardExpanded = !cardExpanded }
                    ) {
                        Icon(
                            Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    }
                }
            }

            if (cardExpanded) {
                val blocks by loadBlocks(heat.id)
                    .collectAsStateWithLifecycle(
                        initialValue = emptyMap()
                    )

                Heatmap(
                    heat = heat,
                    blocks = blocks,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
            }
        }
    }

    DropdownMenu(
        expanded = menuOpened,
        onDismissRequest = { menuOpened = false },
        modifier = Modifier
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.edit_plan
                    )
                )
            },
            onClick = {
                onEditClicked(heat.id)
            },
            leadingIcon = {
                Icon(
                    Icons.TwoTone.Edit,
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(
                        id = R.string.delete_plan
                    )
                )
            },
            onClick = {
                onDeleteClicked(heat)
                menuOpened = false
            },
            leadingIcon = {
                Icon(
                    Icons.TwoTone.Delete,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun Heatmap(
    heat: Heat,
    blocks: Map<Column, List<Block>>,
    modifier: Modifier = Modifier
) {
    val ratioValue = heat.columns.toFloat() / heat.rows

    Row(
        modifier = modifier
            .aspectRatio(ratioValue)
    ) {
        for (column in blocks) {

            Column(modifier = Modifier.weight(1f)) {
                for (block in column.value) {

                    Block(
                        block = block,
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
            )
        }
    }
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