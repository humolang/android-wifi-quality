package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.DEFAULT_HEAT_ID
import com.humolang.wifiless.data.datasources.model.WifiCapabilities
import com.humolang.wifiless.data.datasources.model.WifiProperties
import com.humolang.wifiless.ui.states.LinkSpeedGraphState
import com.humolang.wifiless.ui.states.RssiGraphState
import com.humolang.wifiless.ui.viewmodels.StartViewModel
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navigateToPlanning: (Long) -> Unit,
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
                startViewModel = startViewModel,
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
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
//                    imageVector = Icons.TwoTone.MoreVert,
//                    contentDescription = stringResource(id = R.string.menu)
//                )
//            }
//        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
private fun StartContent(
    navigateToPlanning: (Long) -> Unit,
    navigateToHeats: () -> Unit,
    startViewModel: StartViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        RssiGraph(
            dequeCapacity = startViewModel.dequeCapacity,
            rssiGraphState = startViewModel.rssiGraphState,
            latestRssi = startViewModel.latestRssi,
            rssiValues = startViewModel.rssiValues,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        LinkSpeedGraph(
            dequeCapacity = startViewModel.dequeCapacity,
            linkSpeedGraphState = startViewModel.linkSpeedGraphState,
            latestLinkSpeed = startViewModel.latestLinkSpeed,
            linkSpeedUnits = startViewModel.linkSpeedUnits,
            linkSpeedValues = startViewModel.linkSpeedValues,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        ToolsButtons(
            navigateToPlanning = navigateToPlanning,
            navigateToHeats = navigateToHeats,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        WifiCapabilities(
            wifiCapabilities = startViewModel.wifiCapabilities,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        WifiProperties(
            wifiProperties = startViewModel.wifiProperties,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun RssiGraph(
    dequeCapacity: Int,
    rssiGraphState: StateFlow<RssiGraphState>,
    latestRssi: StateFlow<Int>,
    rssiValues: StateFlow<ArrayDeque<Int>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by remember {
                mutableStateOf(true)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.rssi),
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))

                val rssi by latestRssi
                    .collectAsStateWithLifecycle()

                Text(
                    text = stringResource(
                        id = R.string.rssi_dbm,
                        rssi
                    ),
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                val graphState by rssiGraphState
                    .collectAsStateWithLifecycle()

                GraphDrawer(
                    pointsFlow = rssiValues,
                    dequeCapacity = dequeCapacity,
                    horizontalCapacity = graphState.rssiHorizontalCapacity,
                    verticalCapacity = graphState.minRssi,
                    labelX = stringResource(id = R.string.label_x_time),
                    labelY = stringResource(id = R.string.label_y_rssi),
                    valueAxisAsc = false,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .aspectRatio(3 / 2f)
                )
            }
        }
    }
}

@Composable
private fun LinkSpeedGraph(
    dequeCapacity: Int,
    linkSpeedGraphState: StateFlow<LinkSpeedGraphState>,
    latestLinkSpeed: StateFlow<Int>,
    linkSpeedValues: StateFlow<ArrayDeque<Int>>,
    linkSpeedUnits: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by remember {
                mutableStateOf(false)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.link_speed),
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))

                val linkSpeed by latestLinkSpeed
                    .collectAsStateWithLifecycle()

                Text(
                    text = stringResource(
                        id = R.string.link_speed_value,
                        linkSpeed,
                        linkSpeedUnits
                    ),
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                val graphState by linkSpeedGraphState
                    .collectAsStateWithLifecycle()

                GraphDrawer(
                    pointsFlow = linkSpeedValues,
                    dequeCapacity = dequeCapacity,
                    horizontalCapacity = graphState.linkSpeedHorizontalCapacity,
                    verticalCapacity = graphState.maxLinkSpeed,
                    labelX = stringResource(id = R.string.label_x_time),
                    labelY = stringResource(
                        id = R.string.label_y_speed,
                        linkSpeedUnits
                    ),
                    valueAxisAsc = true,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .aspectRatio(3 / 2f)
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun GraphDrawer(
    pointsFlow: StateFlow<ArrayDeque<Int>>,
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

        val points by pointsFlow
            .collectAsStateWithLifecycle()

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

@Composable
private fun ToolsButtons(
    navigateToPlanning: (Long) -> Unit,
    navigateToHeats: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.tools_buttons),
                style = MaterialTheme.typography.titleLarge
            )

            Row(modifier = Modifier.padding(top = 8.dp)) {
                val buttonShape = MaterialTheme.shapes.small

                ElevatedButton(
                    onClick = {
                        navigateToPlanning(DEFAULT_HEAT_ID)
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f),
                    shape = buttonShape
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.twotone_architecture_24
                            ),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.room_plan),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                ElevatedButton(
                    onClick = navigateToHeats,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    shape = buttonShape
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.twotone_map_24
                            ),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.heatmaps),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WifiCapabilities(
    wifiCapabilities: StateFlow<WifiCapabilities>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by remember {
                mutableStateOf(true)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.capabilities),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    val textStyle = MaterialTheme.typography.bodyLarge

                    val capabilities by wifiCapabilities
                        .collectAsStateWithLifecycle()

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.wifi_standard
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = stringResource(
                                capabilities.wifiStandardStringId
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.security_type
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = stringResource(
                                id = capabilities.securityTypeStringId
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.frequency),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = stringResource(
                                id = R.string.frequency_value,
                                capabilities.frequency,
                                capabilities.frequencyUnits
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.downstream_bandwidth),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = stringResource(
                                id = R.string.bandwidth_kbps,
                                capabilities.downstreamBandwidthKbps
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.upstream_bandwidth),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = stringResource(
                                id = R.string.bandwidth_kbps,
                                capabilities.upstreamBandwidthKbps
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.has_hidden_ssid
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = stringResource(
                                id = if (capabilities.hasHiddenSsid)
                                    R.string.yes_string
                                else R.string.no_string
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.ssid
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = capabilities.ssid,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.bssid),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = capabilities.bssid,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WifiProperties(
    wifiProperties: StateFlow<WifiProperties>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by remember {
                mutableStateOf(true)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.properties),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    val textStyle = MaterialTheme.typography.bodyLarge

                    val properties by wifiProperties
                        .collectAsStateWithLifecycle()

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.ip_address
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = properties.ipAddress,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.nat64_prefix
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = properties.nat64Prefix,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.interface_name
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = properties.interfaceName,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.dhcp_server_address
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = properties.dhcpServer,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.dns_servers
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        ) {
                            for (server in properties.dnsServers) {
                                Text(
                                    text = server,
                                    modifier = Modifier
                                        .padding(top = 4.dp),
                                    style = textStyle
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.http_proxy
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                        Text(
                            text = properties.httpProxy,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            style = textStyle
                        )
                    }
                }
            }
        }
    }
}