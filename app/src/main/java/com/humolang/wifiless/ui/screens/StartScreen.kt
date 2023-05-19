package com.humolang.wifiless.ui.screens

import android.net.wifi.WifiInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.ui.viewmodels.StartViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navigateToPlanning: () -> Unit,
    navigateToHeats: () -> Unit,
    startViewModel: StartViewModel =
        viewModel(factory = StartViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),
        topBar = { StartTopBar(scrollBehavior) },
        content = { innerPadding ->
            StartContent(
                navigateToPlanning = navigateToPlanning,
                navigateToHeats = navigateToHeats,
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                startViewModel = startViewModel
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.twotone_app_icon_24),
                    contentDescription = null
                )
                Text(
                    stringResource(id = R.string.app_name),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
//        navigationIcon = {
//            IconButton(onClick = { /* doSomething() */ }) {
//                Icon(
//                    imageVector = Icons.TwoTone.Menu,
//                    contentDescription = stringResource(id = R.string.menu)
//                )
//            }
//        },
//        actions = {
//            IconButton(onClick = { /* doSomething() */ }) {
//                Icon(
//                    imageVector = Icons.TwoTone.Settings,
//                    contentDescription = stringResource(id = R.string.settings)
//                )
//            }
//        }
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
private fun StartContent(
    navigateToPlanning: () -> Unit,
    navigateToHeats: () -> Unit,
    modifier: Modifier = Modifier,
    startViewModel: StartViewModel =
        viewModel(factory = StartViewModel.Factory)
) {
    val startUiState by startViewModel
        .startUiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.padding(16.dp)) {
        RssiGraph(
            latestRssi = startUiState.latestRssi,
            rssiValues = startUiState.rssiValues,
            dequeCapacity = startUiState.dequeCapacity,
            horizontalCapacity = startUiState.rssiHorizontalCapacity,
            verticalCapacity = startUiState.minRssi,
            modifier = Modifier.padding(top = 16.dp)
        )
        SpeedGraph(
            latestSpeed = startUiState.latestSpeed,
            speedValues = startUiState.speedValues,
            dequeCapacity = startUiState.dequeCapacity,
            horizontalCapacity = startUiState.linkSpeedHorizontalCapacity,
            verticalCapacity = startUiState.maxLinkSpeed,
            modifier = Modifier.padding(top = 16.dp)
        )
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = navigateToPlanning
            ) {
                Text(text = "to planning")
            }
            Button(
                onClick = navigateToHeats,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "to heats")
            }
        }
        Text(
            text = "IP Address: ${startUiState.ipAddress}",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun RssiGraph(
    latestRssi: Int,
    rssiValues: ArrayDeque<Int>,
    dequeCapacity: Int,
    horizontalCapacity: Int,
    verticalCapacity: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "$latestRssi dBm")
        GraphDrawer(
            points = rssiValues,
            dequeCapacity = dequeCapacity,
            horizontalCapacity = horizontalCapacity,
            verticalCapacity = verticalCapacity,
            labelX = stringResource(id = R.string.label_x_time),
            labelY = stringResource(id = R.string.label_y_rssi),
            valueAxisAsc = false,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(256.dp)
        )
    }
}

@Composable
private fun SpeedGraph(
    latestSpeed: Int,
    speedValues: ArrayDeque<Int>,
    dequeCapacity: Int,
    horizontalCapacity: Int,
    verticalCapacity: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "$latestSpeed ${WifiInfo.LINK_SPEED_UNITS}")
        GraphDrawer(
            points = speedValues,
            dequeCapacity = dequeCapacity,
            horizontalCapacity = horizontalCapacity,
            verticalCapacity = verticalCapacity,
            labelX = stringResource(id = R.string.label_x_time),
            labelY = stringResource(id = R.string.label_y_speed),
            valueAxisAsc = true,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(256.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GraphDrawerPreview() {
    val dequeCapacity = 60
    val horizontalCapacity = 60
    val rssiVerticalCapacity = -127
    val speedVerticalCapacity = 144

    val rssiPoints = ArrayDeque<Int>(horizontalCapacity)
    val speedPoints = ArrayDeque<Int>(horizontalCapacity)

    for (index in 0 until horizontalCapacity) {
        val rssi = Random.nextInt(
            from = rssiVerticalCapacity + 9,
            until = -9
        )

        val speed = Random.nextInt(
            from = 10,
            until = speedVerticalCapacity - 9
        )

        rssiPoints.add(rssi)
        speedPoints.add(speed)
    }

    Column {
        GraphDrawer(
            points = rssiPoints,
            dequeCapacity = dequeCapacity,
            verticalCapacity = rssiVerticalCapacity,
            horizontalCapacity = horizontalCapacity,
            labelX = "Time, s",
            labelY = "RSSI, dBm",
            valueAxisAsc = false,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(256.dp)
        )
        GraphDrawer(
            points = speedPoints,
            dequeCapacity = dequeCapacity,
            verticalCapacity = speedVerticalCapacity,
            horizontalCapacity = horizontalCapacity,
            labelX = "Time, s",
            labelY = "Speed, Mbps",
            valueAxisAsc = true,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(256.dp)
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun GraphDrawer(
    points: ArrayDeque<Int>,
    dequeCapacity: Int,
    horizontalCapacity: Int,
    verticalCapacity: Int,
    labelX: String,
    labelY: String,
    valueAxisAsc: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme
        .tertiaryContainer

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            )
    ) {
        val lineColor = MaterialTheme.colorScheme
            .tertiary
            .copy(alpha = 0.4f)
        val graphColor = MaterialTheme.colorScheme
            .onTertiaryContainer
            .copy(alpha = 0.6f)

        val textMeasurer = rememberTextMeasurer()
        val textStyle = MaterialTheme.typography
            .labelSmall

        Canvas(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .drawWithCache {
                    val graphWidthPx = 2.dp.toPx()
                    val graph = createGraph(
                        points = points,
                        dequeCapacity = dequeCapacity,
                        verticalCapacity = verticalCapacity,
                        valueAxisAsc = valueAxisAsc,
                        canvasSize = size
                    )

                    onDrawBehind {
                        drawPath(
                            path = graph,
                            color = graphColor,
                            style = Stroke(
                                width = graphWidthPx,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
        ) {
            val lineWidthPx = 1.dp.toPx()
            val textXOffsetPx = 2.dp.toPx()

            drawRect(
                color = lineColor,
                style = Stroke(lineWidthPx)
            )

            drawHorizontalsWithLabels(
                verticalCapacity = verticalCapacity,
                lineColor = lineColor,
                lineWidthPx = lineWidthPx,
                textMeasurer = textMeasurer,
                textXOffsetPx = textXOffsetPx,
                textStyle = textStyle,
                valueAxisAsc = valueAxisAsc
            )

            drawVerticalsWithLabels(
                horizontalCapacity = horizontalCapacity,
                lineColor = lineColor,
                lineWidthPx = lineWidthPx,
                textMeasurer = textMeasurer,
                textXOffsetPx = textXOffsetPx,
                textStyle = textStyle,
                valueAxisAsc = valueAxisAsc
            )

            if (valueAxisAsc) {
                drawSingleLabelsAsc(
                    textMeasurer = textMeasurer,
                    textXOffsetPx = textXOffsetPx,
                    textStyle = textStyle,
                    labelX = labelX,
                    labelY = labelY
                )
            } else {
                drawSingleLabelsDesc(
                    textMeasurer = textMeasurer,
                    textXOffsetPx = textXOffsetPx,
                    textStyle = textStyle,
                    labelX = labelX,
                    labelY = labelY
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawHorizontalsWithLabels(
    verticalCapacity: Int,
    lineColor: Color,
    lineWidthPx: Float,
    textMeasurer: TextMeasurer,
    textXOffsetPx: Float,
    textStyle: TextStyle,
    valueAxisAsc: Boolean
) {
    val horizontals = if (size.width > size.height) {
        size.width / size.height
    } else {
        size.height / size.width
    }.toInt() * 3

    val horizontalsInterval = size.height / (horizontals + 1)
    val valueInterval = verticalCapacity / (horizontals + 1)

    repeat(horizontals) { number ->
        val y = horizontalsInterval * (number + 1)

        drawLine(
            color = lineColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = lineWidthPx
        )

        val text = if (valueAxisAsc) {
            valueInterval * (horizontals - number)
        } else {
            valueInterval * (number + 1)
        }.toString()

        drawText(
            textMeasurer = textMeasurer,
            text = text,
            topLeft = Offset(0f + textXOffsetPx, y),
            style = textStyle
        )
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawVerticalsWithLabels(
    horizontalCapacity: Int,
    lineColor: Color,
    lineWidthPx: Float,
    textMeasurer: TextMeasurer,
    textXOffsetPx: Float,
    textStyle: TextStyle,
    valueAxisAsc: Boolean
) {
    val verticals = if (size.width > size.height) {
        size.width / size.height
    } else {
        size.height / size.width
    }.toInt() * 4

    val verticalsInterval = size.width / (verticals + 1)
    val timeInterval = horizontalCapacity / (verticals + 1)

    repeat(verticals) { number ->
        val x = verticalsInterval * (number + 1)

        drawLine(
            color = lineColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = lineWidthPx
        )

        val text = (timeInterval * (number + 1))
            .toString()

        val y = if (valueAxisAsc) {
            val labelHeight = textMeasurer
                .measure(text, textStyle).size.height
            size.height - labelHeight
        } else {
            0f
        }

        drawText(
            textMeasurer = textMeasurer,
            text = text,
            topLeft = Offset(
                x = x + textXOffsetPx,
                y = y
            ),
            style = textStyle
        )
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawSingleLabelsAsc(
    textMeasurer: TextMeasurer,
    textXOffsetPx: Float,
    textStyle: TextStyle,
    labelX: String,
    labelY: String
) {
    val labelZero = "0"
    val labelZeroHeight = textMeasurer
        .measure(labelZero, textStyle).size.height

    drawText(
        textMeasurer = textMeasurer,
        text = labelZero,
        topLeft = Offset(
            0f + textXOffsetPx,
            size.height - labelZeroHeight
        ),
        style = textStyle
    )

    val labelXWidth = textMeasurer
        .measure(labelX, textStyle).size.width
    val labelXHeight = textMeasurer
        .measure(labelX, textStyle).size.height

    drawText(
        textMeasurer = textMeasurer,
        text = labelX,
        topLeft = Offset(
            x = size.width - labelXWidth - textXOffsetPx,
            y = size.height - labelXHeight
        ),
        style = textStyle
    )

    drawText(
        textMeasurer = textMeasurer,
        text = labelY,
        topLeft = Offset(
            x = 0f + textXOffsetPx,
            y = 0f
        ),
        style = textStyle
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawSingleLabelsDesc(
    textMeasurer: TextMeasurer,
    textXOffsetPx: Float,
    textStyle: TextStyle,
    labelX: String,
    labelY: String
) {
    drawText(
        textMeasurer = textMeasurer,
        text = "0",
        topLeft = Offset(0f + textXOffsetPx, 0f),
        style = textStyle
    )

    val labelXWidth = textMeasurer
        .measure(labelX, textStyle).size.width

    drawText(
        textMeasurer = textMeasurer,
        text = labelX,
        topLeft = Offset(
            x = size.width - labelXWidth,
            y = 0f
        ),
        style = textStyle
    )

    val labelYHeight = textMeasurer
        .measure(labelY, textStyle).size.height

    drawText(
        textMeasurer = textMeasurer,
        text = labelY,
        topLeft = Offset(
            x = 0f + textXOffsetPx,
            y = size.height - labelYHeight
        ),
        style = textStyle
    )
}

private fun createGraph(
    points: ArrayDeque<Int>,
    dequeCapacity: Int,
    verticalCapacity: Int,
    valueAxisAsc: Boolean,
    canvasSize: Size
): Path {
    val graph = Path()

    points.forEachIndexed { index, point ->
        val x = canvasSize.width *
                ((index + 1).toFloat() / dequeCapacity)

        val y = if (valueAxisAsc) {
            canvasSize.height *
                    ((verticalCapacity - point).toFloat() / verticalCapacity)
        } else {
            canvasSize.height *
                    (point.toFloat() / verticalCapacity)
        }

        if (index == 0) {
            graph.moveTo(0f, y)
        } else {
            graph.lineTo(x, y)
        }
    }

    return graph
}