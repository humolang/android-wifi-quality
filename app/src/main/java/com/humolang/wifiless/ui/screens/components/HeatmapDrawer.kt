package com.humolang.wifiless.ui.screens.components

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.datasources.model.BlockType
import kotlin.random.Random

@Preview(showBackground = true)
@Composable
private fun HeatmapPreview() {
    val heat = Heat(
        columns = Random.nextInt(3, 12),
        rows = Random.nextInt(3, 12),
    )

    val blocks = mutableMapOf<Column, List<Block>>()
    repeat(heat.columns) { x ->
        val column = Column(
            heatId = heat.id,
            x = x
        )

        val list = mutableListOf<Block>()
        repeat(heat.rows) { y ->
            val rssi = Random
                .nextInt(-127, 0)

            val randomType = Random
                .nextInt(0, 10)
            val type = when (randomType) {
                0 -> BlockType.ARMCHAIR
                1 -> BlockType.CHAIR
                2 -> BlockType.COMPUTER
                3 -> BlockType.DOOR
                4 -> BlockType.ROUTER
                5 -> BlockType.TABLE
                6 -> BlockType.TV
                7 -> BlockType.WINDOW
                8 -> BlockType.WALL
                else -> BlockType.FREE
            }

            val block = Block(
                columnId = column.id,
                y = y,
                type = type,
                rssi = rssi
            )

            list.add(block)
        }

        blocks[column] = list
    }

    Heatmap(
        heat = heat,
        blocks = blocks,
        modifier = Modifier
            .padding(16.dp)
    )
}

@Composable
fun TransformableHeatmap(
    heat: Heat,
    blocks: Map<Column, List<Block>>,
    modifier: Modifier = Modifier,
    onBlockClicked: (Block) -> Unit = {  },
    onBlockLongClicked: (Column, Block) -> Unit = { column, block -> },
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    Heatmap(
        heat = heat,
        blocks = blocks,
        onBlockClicked = onBlockClicked,
        onBlockLongClicked = onBlockLongClicked,
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = state)
    )
}

@Composable
fun Heatmap(
    heat: Heat,
    blocks: Map<Column, List<Block>>,
    modifier: Modifier = Modifier,
    onBlockClicked: (Block) -> Unit = {  },
    onBlockLongClicked: (Column, Block) -> Unit = { column, block -> },
) {
    val ratioValue = heat.columns.toFloat() / heat.rows

    Row(
        modifier = modifier
            .aspectRatio(ratioValue)
    ) {
        for (column in blocks) {

            Column(modifier = Modifier.weight(1f)) {
                for (block in column.value) {

                    RssiBlock(
                        block = block,
                        onBlockClicked = onBlockClicked,
                        onBlockLongClicked = { selected ->
                            onBlockLongClicked(column.key, selected)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                }
            }
        }
    }
}