package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
import com.humolang.wifiless.ui.viewmodels.MappingViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

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
private fun RssiHorizontalScale(
    minRssi: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val startColor = MaterialTheme.colorScheme
            .tertiaryContainer.copy(
                red = 0f,
                green = 0f
            )
        val endColor = MaterialTheme.colorScheme
            .tertiaryContainer.copy(
                red = 1f,
                green = 1f
            )

        val startLabel = stringResource(
            id = R.string.rssi_dbm,
            minRssi
        )
        val endLabel = stringResource(
            id = R.string.rssi_dbm,
            0
        )

        HorizontalColorScale(
            startLabel = startLabel,
            endLabel = endLabel,
            startColor = startColor,
            endColor = endColor,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun HorizontalColorScale(
    startLabel: String,
    endLabel: String,
    startColor: Color,
    endColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val horizontalGradient = Brush
            .horizontalGradient(
                colors = listOf(
                    startColor,
                    endColor
                )
            )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            drawLine(
                brush = horizontalGradient,
                start = Offset(
                    0f,
                    size.height / 2
                ),
                end = Offset(
                    size.width,
                    size.height / 2
                ),
                strokeWidth = size.height
            )
        }

        val textStyle = MaterialTheme.typography
            .labelSmall

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = startLabel,
                style = textStyle
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = endLabel,
                style = textStyle
            )
        }
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
        Heatmap(
            heatFlow = heatFlow,
            blocksFlow = blocksFlow,
            onBlockClicked = onBlockClicked,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun Heatmap(
    heatFlow: StateFlow<Heat>,
    blocksFlow: StateFlow<Map<Column, List<Block>>>,
    onBlockClicked: (Block) -> Unit,
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
                        onBlockClicked = onBlockClicked,
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
    onBlockClicked: (Block) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasRssi = abs(block.rssi) in 0..100
    val rssiColor = abs(block.rssi.toFloat()) / 100

    val borderColor = MaterialTheme.colorScheme
        .onTertiaryContainer

    val tertiaryRectangle = MaterialTheme.colorScheme
        .tertiaryContainer
    val rectangleColor = tertiaryRectangle.copy(
        red = if (hasRssi)
            rssiColor
        else tertiaryRectangle.red,

        green = if (hasRssi)
            rssiColor
        else tertiaryRectangle.green,
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
                    .clickable {
                        onBlockClicked(block)
                    }
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
                    .clickable {
                        onBlockClicked(block)
                    }
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
                    .clickable {
                        onBlockClicked(block)
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
                RoundedCornerShape(6.dp)
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