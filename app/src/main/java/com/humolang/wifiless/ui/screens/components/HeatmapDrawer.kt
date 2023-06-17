package com.humolang.wifiless.ui.screens.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    strokeWidth: Dp = 1.dp,
    onBlockClicked: (Block) -> Unit = {  },
    onBlockLongClicked: (Column, Block) -> Unit = { column, block -> },
) {
    val context = LocalContext.current
    val ratioValue = heat.columns
        .toFloat() / heat.rows

    val onTertiaryContainer = MaterialTheme
        .colorScheme
        .onTertiaryContainer
    val tertiaryContainer = MaterialTheme
        .colorScheme
        .tertiaryContainer

    Canvas(
        modifier = modifier
            .aspectRatio(ratioValue)
            .pointerInput(ratioValue) {
                detectTapGestures(
                    onTap = { offset ->
                        val blockSize = Size(
                            width = size.height
                                .toFloat() / heat.rows,
                            height = size.width
                                .toFloat() / heat.columns
                        )

                        val x = (offset.x / blockSize.width).toInt()
                        val y = (offset.y / blockSize.height).toInt()

                        val column = blocks.keys.find { it.x == x }
                        val block = blocks[column]?.find { it.y == y }

                        if (block != null) {
                            onBlockClicked(block)
                        }
                    },
                    onLongPress = { offset ->
                        val blockSize = Size(
                            width = size.height
                                .toFloat() / heat.rows,
                            height = size.width
                                .toFloat() / heat.columns
                        )

                        val x = (offset.x / blockSize.width).toInt()
                        val y = (offset.y / blockSize.height).toInt()

                        val column = blocks.keys.find { it.x == x }
                        val block = blocks[column]?.find { it.y == y }

                        if (column != null && block != null) {
                            onBlockLongClicked(column, block)
                        }
                    }
                )
            }
    ) {
        drawRect(
            color = tertiaryContainer
        )

        val size = Size(
            width = size.height / heat.rows,
            height = size.width / heat.columns
        )

        for (column in blocks) {
            val x = column.key.x * size.width

            for (block in column.value) {
                val topLeft = Offset(
                    x = x,
                    y = block.y * size.height
                )

                drawBlock(
                    context = context,
                    block = block,
                    border = onTertiaryContainer,
                    background = tertiaryContainer,
                    topLeft = topLeft,
                    size = size,
                    strokeWidth = strokeWidth.toPx()
                )
            }
        }
    }

//    Row(
//        modifier = modifier
//            .aspectRatio(ratioValue)
//    ) {
//        for (column in blocks) {
//
//            Column(modifier = Modifier.weight(1f)) {
//                for (block in column.value) {
//
//                    RssiBlock(
//                        block = block,
//                        onBlockClicked = onBlockClicked,
//                        onBlockLongClicked = { selected ->
//                            onBlockLongClicked(column.key, selected)
//                        },
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .weight(1f)
//                    )
//                }
//            }
//        }
//    }
}

private fun DrawScope.drawBlock(
    context: Context,
    block: Block,
    border: Color,
    background: Color,
    topLeft: Offset,
    size: Size,
    strokeWidth: Float
) {
    drawRect(
        color = border,
        topLeft = topLeft,
        size = size,
        style = Stroke(strokeWidth)
    )

    if (block.rssi > Int.MIN_VALUE) {
        drawRssi(
            rssi = block.rssi,
            color = background,
            topLeft = topLeft,
            size = size
        )
    }

    if (block.type == BlockType.WALL) {
        drawWall(
            color = border,
            topLeft = topLeft,
            size = size,
            strokeWidth = strokeWidth
        )
    } else if (block.type != BlockType.FREE) {
        drawImage(
            image = convertImageVector(
                context = context,
                drawableId = block.drawableId,
                size = size
            ),
            topLeft = topLeft
        )
    }
}

private fun DrawScope.drawRssi(
    rssi: Int,
    color: Color,
    topLeft: Offset,
    size: Size
) {
    val rssiRatio = rssi / -127F
    val rssiColor = color.copy(
        //alpha = 1 - rssiColor,
        red = rssiRatio,
        green = 1f - rssiRatio,
        blue = 0f
    )

    drawRect(
        color = rssiColor,
        topLeft = topLeft,
        size = size
    )
}

private fun DrawScope.drawWall(
    color: Color,
    topLeft: Offset,
    size: Size,
    strokeWidth: Float
) {
    val times = 6

    val intervalX = size.width / times
    val intervalY = size.height / times

    repeat(times) { number ->
        drawLine(
            color = color,
            start = Offset(
                x = topLeft.x,
                y = topLeft.y + size.height - (intervalY * number)
            ),
            end = Offset(
                x = topLeft.x + size.width - (intervalX * number),
                y = topLeft.y
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                x = topLeft.x + (intervalX * number),
                y = topLeft.y + size.height
            ),
            end = Offset(
                x = topLeft.x + size.width,
                y = topLeft.y + (intervalY * number)
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                x = topLeft.x + (intervalY * number),
                y = topLeft.y
            ),
            end = Offset(
                x = topLeft.x + size.width,
                y = topLeft.y + size.height - (intervalX * number)
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                x = topLeft.x,
                y = topLeft.y + (intervalY * number)
            ),
            end = Offset(
                x = topLeft.x + size.width - (intervalX * number),
                y = topLeft.y + size.height
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun convertImageVector(
    context: Context,
    @DrawableRes drawableId: Int,
    size: Size
): ImageBitmap {
    val drawable = ContextCompat
        .getDrawable(context, drawableId)
    val bitmap = Bitmap.createBitmap(
        size.width.toInt(),
        size.height.toInt(),
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    drawable?.setBounds(
        0,
        0,
        canvas.width,
        canvas.height
    )
    drawable?.draw(canvas)

    return bitmap.asImageBitmap()
}