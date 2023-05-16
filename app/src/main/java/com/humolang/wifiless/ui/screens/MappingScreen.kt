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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.ui.viewmodels.MappingViewModel
import kotlin.math.abs

@Composable
fun MappingScreen(
    heatId: Int,
    navigateToStart: () -> Unit,
    mappingViewModel: MappingViewModel =
        viewModel(factory = MappingViewModel.Factory)
) {
    Text(text = "mapping screen heatId = $heatId")

    val mappingUiState by mappingViewModel
        .mappingUiState.collectAsStateWithLifecycle()

    mappingViewModel.loadBlocks(heatId)

    val heat = mappingUiState.heat
    val blocks = mappingUiState.blocks

    if (heat != null) {
        MappingField(
            columns = heat.length,
            rows = heat.width,
            blocks = blocks,
            onBlockClicked = { block ->
                mappingViewModel.checkRssi(block)
            },
            navigateToStart = navigateToStart,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        )
    }
}

@Composable
private fun MappingField(
    columns: Int,
    rows: Int,
    blocks: Map<Column, List<Block>>,
    onBlockClicked: (Block) -> Unit,
    navigateToStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        HeatMap(
            columns = columns,
            rows = rows,
            blocks = blocks,
            onBlockClicked = onBlockClicked
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(
                onClick = { navigateToStart() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "to start")
            }
        }
    }
}

@Composable
private fun HeatMap(
    columns: Int,
    rows: Int,
    blocks: Map<Column, List<Block>>,
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

    val ratioValue by remember {
        mutableStateOf(columns.toFloat() / rows)
    }

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

            Column(modifier = Modifier.weight(1F)) {
                for (block in column.value) {

                    Block(
                        block = block,
                        onBlockClicked = onBlockClicked,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
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
    val tertiaryBorder = MaterialTheme.colorScheme.tertiary
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

    Canvas(
        modifier = modifier
            .border(
                2.dp,
                borderColor,
                RoundedCornerShape(4.dp)
            )
            .clickable {
                onBlockClicked(block)
            },
        onDraw = {
            drawRoundRect(
                color = rectangleColor
            )
        }
    )
}